package com.sulaco.fuse.config.route;

import java.util.Map;
import java.util.Optional;

import akka.actor.ActorRef;

public interface Route {

	public ActorRef getHandler();
	
	public Map<String, String> getParams();
	
	public Optional<String> getParam(String name);
	
}
