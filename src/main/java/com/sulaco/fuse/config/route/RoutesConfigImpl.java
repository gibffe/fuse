package com.sulaco.fuse.config.route;


import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import akka.actor.ActorRef;
import static com.sulaco.fuse.util.Tools.*;
import com.sulaco.fuse.config.ConfigSource;
import com.sulaco.fuse.config.actor.ActorFactory;
import com.sulaco.fuse.config.route.RouteSegment.RouteSegmentBuilder;
import com.typesafe.config.ConfigValue;

@Component
@SuppressWarnings({"unchecked","unused"})
public class RoutesConfigImpl implements RoutesConfig {

	@Autowired ActorFactory factory;
	
	RouteSegment root;
	
	@Autowired protected ConfigSource configSource;
	
	public RoutesConfigImpl() {
		root = RouteSegment.builder().build();
	}
		
	@Override
	public void parse() {		
		log.info("[fuse] Parsing routes");
		
		// parse actors section
		parseActorDefs();
		
		// parse routes section
		parseRouteDefs();
	}
	
	protected void parseActorDefs() {
		Set<Entry<String, ConfigValue>> actorDefs 
			= configSource.getConfig()
						  .getObject("actors")
						  .entrySet();

		for (Entry<String, ConfigValue> entry : actorDefs) {
			processActorEntry(entry);
		}
	}
	
	protected void parseRouteDefs() {
		Set<Entry<String, ConfigValue>> routeDefs
			= configSource.getConfig()
						  .getObject("routes")
						  .entrySet();

		for (Entry<String, ConfigValue> entry : routeDefs) {
			processRouteEntry(entry);
		}
	}
	
	protected void processActorEntry(Entry<String, ConfigValue> entry) {
	
		// extract actor class
		String actorClass = entry.getKey();
		
		// extract definition
		Map<String, Object> map = (Map<String, Object>) entry.getValue().unwrapped();
		
		String id = (String) map.get("id");
		int spinCount = parseSpinCount(map.get("spin"));
		
		factory.getLocalActor(id, actorClass, spinCount);
	}

	protected void processRouteEntry(Entry<String, ConfigValue> entry) {
	
		// process actor definition
		Optional<RouteHandler> handler = processActorDefinition(entry.getValue());
		
		// process path definition
		processPathDefinition(entry.getKey(), handler); 

	}
	
	protected Optional<RouteHandler> processActorDefinition(ConfigValue cvalue) {
		
		try {
			// extract actor definition
			Map<String, Object> map = (Map<String, Object>) cvalue.unwrapped();

			String actorRef   = (String) map.get("ref");
			String methodName = (String) map.get("call");
			String actorClass = (String) map.get("class");
			
			Optional<ActorRef> ref = Optional.empty();
			
			if (!StringUtils.isEmpty(actorRef)) {
				// get actor by reference supplied
				ref = factory.getLocalActorByRef(actorRef);
			}
			
			if (!StringUtils.isEmpty(actorClass)) {
				// create new actor using class specified
				ref = factory.getLocalActor(actorClass);
				
			}

			return Optional.ofNullable(
					new RouteHandler(
							ref, 
							methodName
						)
					);
		}
		catch (Exception ex) {
			log.warn("Error parsing actor definition: "+cvalue.render());
		}
		
		return empty();
	}

	
	protected void processPathDefinition(String path, Optional<RouteHandler> handler) {

		if (handler.isPresent()) {
			RouteSegment segment = root;
			RouteSegmentBuilder builder = RouteSegment.builder();
	
			String[] split = path.split("/");
	
			// convert REST URI segments to stream & process one by one
			Iterator<String> it = Arrays.stream(split).iterator();

			it.next();
			while (it.hasNext()) {
				String value = it.next();
				segment = processSegment(value, it.hasNext(), builder, segment, handler.get());
				saveSegment(segment);
				
				builder.reset();
			}
		}
	}
	
	private RouteSegment processSegment(String value, boolean more, RouteSegmentBuilder builder, RouteSegment parent, RouteHandler handler) {
		
		RouteSegment segment = null;
		
		builder.withValue(value);
		builder.withParent(parent);
		
		// parameter detect
		if (value.charAt(0) == '<' && value.charAt(value.length() -1) == '>') {
			builder.withValue(extractParamName(value));
			builder.withDynamic(true);
		}
		
		if (!more) {
			// last segment, assign handling actor
			builder.withHandler(handler);
		}
		segment = builder.build();
		
		// check if parent already has a segment for this very segment key
		// if it does, assign handler if necessary
		RouteSegment existing = parent.children.get(segment.key());
		if (existing != null && !existing.handler.isPresent()) {
			existing.handler = segment.handler;
		}

		return existing != null 
			            ? existing 
			            : segment;
	}
	
	private RouteSegment saveSegment(RouteSegment segment) {
		
		return segment.parent()
					  .get()
					  .addChild(segment)
		;
	}
	
	private int parseSpinCount(Object value) {
		
		if (value != null) {
			if (value instanceof Integer) {
				return (Integer) value;
			}
			throw new IllegalArgumentException("wrong spin count");
		}
		
		return defaultSpinCount();
	}
	
	private int defaultSpinCount() {
		return configSource.getConfig().getInt("fuse.spin.default");
	}
	
	@Override
	public Optional<Route> getFuseRoute(String requestUri) {

		Route route = null;
		
		try {
			URI uri = URI.create(requestUri);
			
			// extract path & split into REST segments
			String path = uri.getPath();
			String[] segments = path.split("/");
			
			if (segments.length > 1) {
				// perform pattern matching against existing routes config
				route = find(segments);
				if (route == null) {
					log.warn("Matchng handler for '{}' was not found.", requestUri);
				}
			}
		}
		catch (Exception ex) {
			log.warn("Error parsing request uri", ex);
		}
		
		return Optional.ofNullable(route);
	}
	
	private String extractParamName(String value) {
		return value.split("<")[1].split(">")[0];
	}
	
	protected Route find(String[] segments) {
		
		Optional<RouteSegment> next;
		RouteSegment current = this.root;
		
		// holds parameters captured during route resolution
		Map<String, String> params = new HashMap<>();

		for(int i = 1; i < segments.length; i++) {

			next = current.child(segments[i]);
			
			if (!next.isPresent()) {
				// look for 'parameter matching' segment
				next = current.child("*");
				if (next.isPresent()) {
					// capture parameter
					params.putIfAbsent(next.get().value(), segments[i]);
				}
			}
			
			if (next.isPresent()) {
				current = next.get();
			}
			else {
				return null;
			}
		}
		
		// construct a route object
		if (current.handler().isPresent()) {
			return RouteImpl.builder()
					        .withHandler(current.handler().get())
					        .withParams(params)
					        .build();
		}
		else {
			return null;
		}
	}
	
	private static final Logger log = LoggerFactory.getLogger(RoutesConfigImpl.class);

}
