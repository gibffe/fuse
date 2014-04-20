package com.sulaco.fuse.config.route;

import java.util.Optional;

import akka.actor.ActorRef;

public class RouteHandler {
	
	Optional<ActorRef> actor;
	
	Optional<String>   methodName;
	
	
	public RouteHandler() {
		actor = Optional.ofNullable(null);
		actor = Optional.ofNullable(null);
	}

	public RouteHandler(Optional<ActorRef> actor, String methodName) {
		this.actor      = actor;
		this.methodName = Optional.ofNullable(methodName);
	}

	public Optional<ActorRef> getActor() {
		return actor;
	}

	public void setActor(Optional<ActorRef> actor) {
		this.actor = actor;
	}


	public Optional<String> getMethodName() {
		return methodName;
	}


	public void setMethodName(Optional<String> methodName) {
		this.methodName = methodName;
	}
	
}
