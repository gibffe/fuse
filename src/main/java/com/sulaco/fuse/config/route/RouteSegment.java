package com.sulaco.fuse.config.route;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class RouteSegment {

	Optional<RouteHandler> handler;
	
	String value;
	
	boolean dynamic = false;
	
	Optional<RouteSegment> parent;
	
	Map<String, RouteSegment> children;
	
	private RouteSegment() {
		this.children = new HashMap<>();
	}
	
	public static RouteSegmentBuilder builder() {
		return new RouteSegmentBuilder();
	}
	
	public static class RouteSegmentBuilder {
		
		private RouteSegment instance;
		
		public RouteSegmentBuilder() {
			this.instance = new RouteSegment();
		}
		
		public RouteSegmentBuilder withHandler(RouteHandler handler) {
			instance.handler = Optional.ofNullable(handler);
			return this;
		}
		
		public RouteSegmentBuilder withValue(String value) {
			instance.value = value;
			return this;
		}
		
		public RouteSegmentBuilder withParent(RouteSegment parent) {
			instance.parent = Optional.ofNullable(parent);
			return this;
		}
		
		public RouteSegmentBuilder withDynamic(boolean dynamic) {
			instance.dynamic = dynamic;
			return this;
		}
		
		public RouteSegment build() {
			
			if (instance.parent == null) {
				instance.parent = Optional.ofNullable(null);
			}
			
			if (instance.handler == null) {
				instance.handler = Optional.ofNullable(null);
			}
			
			return this.instance;
		}
		
		public void reset() {
			this.instance = new RouteSegment();
		}
	}
	
	public Optional<RouteHandler> handler() {
		return handler;
	}

	public String value() {
		return value;
	}

	public boolean dynamic() {
		return dynamic;
	}

	public Optional<RouteSegment> parent() {
		return parent;
	}

	public Optional<RouteSegment> child(String key) {
		return Optional.ofNullable(children.get(key));
	}
	
	public String key() {
		return dynamic ? "*" : value;
	}
	
	public RouteSegment addChild(final RouteSegment child) {
		
		child.parent = Optional.ofNullable(this);
		
		this.children.computeIfPresent(
							child.key(), 
							(key, segment) -> {
								if (child.handler.isPresent()) {
									// copy handler from "duplicate" child segment
									segment.handler = child.handler;
								}
								return segment;
							}
		);

		this.children.putIfAbsent(child.key(), child);
		
		return child;
	}
	
}
