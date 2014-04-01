package com.sulaco.fuse.config.route;


import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import static com.sulaco.fuse.util.Tools.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import akka.actor.ActorRef;

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
		
		Set<Entry<String, ConfigValue>> routes 
			= configSource.getConfig()
						  .getObject("routes")
						  .entrySet();
	
		for (Entry<String, ConfigValue> entry : routes) {
			processConfigEntry(entry);
		}
	}

	protected void processConfigEntry(Entry<String, ConfigValue> entry) {
	
		// process actor definition
		Optional<RouteHandler> handler = processActorDefinition(entry.getValue());
		
		// process path definition
		processPathDefinition(entry.getKey(), handler); 

	}
	
	protected Optional<RouteHandler> processActorDefinition(ConfigValue cvalue) {
		
		try {
			// extract actor definition
			Map<String, Object> map = (Map<String, Object>) cvalue.unwrapped();

			String actorClass, methodName;
			int spinCount = defaultSpinCount();

			spinCount  = parseSpinCount(map.get("spin"));
			actorClass = parseActorClass(map.get("class"));
			methodName = (String) map.get("method");
			
			// create actor/router handler
			ActorRef actor = factory.getLocalActor(actorClass, spinCount);
			
			return Optional.ofNullable(
					new RouteHandler(actor, methodName)
			);
		}
		catch (Exception ex) {
			log.warn("Error parsing actor definition: "+cvalue.render());
		}
		
		return _null();
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
		
		// build & store segment
		return builder.build();
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
	
	private String parseActorClass(Object value) {
		
		if (value != null) {
			if (value instanceof String) {
				return (String) value;
			}
			throw new IllegalArgumentException("wrong actor class");
		}
		
		throw new IllegalArgumentException("wrong actor class");
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
			
			if (segments.length > 0) {
				// perform pattern matching against existing routes config
				route = find(segments);
			}
		}
		catch (Exception ex) {
			log.warn("Error parsing request uri", ex);
		}
		
		return Optional.of(route);
	}
	
	private String extractParamName(String value) {
		// value.split("(<)(>)")[1]
		return value.split("<")[1].split(">")[0];
	}
	
	protected Route find(String[] segments) {
		return null;
	}
	
	private static final Logger log = LoggerFactory.getLogger(RoutesConfigImpl.class);

}
