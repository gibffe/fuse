package com.sulaco.fuse.config.route;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class RouteImpl implements Route {

	RouteHandler handler;
	
	Map<String, String> params;
	
	private RouteImpl() {
		this.params = new HashMap<>();
	}
	
	public static RouteBuilder builder() {
		return new RouteBuilder();
	}
	
	public static class RouteBuilder {
		
		private RouteImpl instance;
		
		public RouteBuilder() {
			this.instance = new RouteImpl();
		}
		
		public RouteBuilder withHandler(RouteHandler handler) {
			instance.handler = handler;
			return this;
		}
		
		public RouteBuilder withParam(String key, String value) {
			instance.params.put(key, value);
			return this;
		}
		
		public RouteBuilder withParams(Map<String, String> params) {
			instance.params.putAll(params);
			return this;
		}
		
		public Route build() {
			
			if (instance.handler == null) {
				throw new IllegalArgumentException("Hanler not set for this route !");
			}
			
			return instance;
		}
	}
	
	@Override
	public RouteHandler getHandler() {
		return handler;
	}

	@Override
	public Map<String, String> getParams() {
		return params;
	}

	@Override
	public Optional<String> getParam(String name) {
		return Optional.ofNullable(params.get(name));
	}

}
