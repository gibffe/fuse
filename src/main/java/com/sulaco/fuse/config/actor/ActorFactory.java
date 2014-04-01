package com.sulaco.fuse.config.actor;

import org.springframework.context.ApplicationContextAware;

import akka.actor.ActorRef;

import com.sulaco.fuse.akka.actor.ActorSystemAware;

public interface ActorFactory extends ActorSystemAware, ApplicationContextAware {

	public ActorRef getLocalActor(String actorClass, int spinCount);
	
}
