package com.sulaco.fuse.config.actor;

import java.util.Optional;

import org.springframework.context.ApplicationContextAware;

import akka.actor.ActorRef;

import com.sulaco.fuse.akka.actor.ActorSystemAware;

public interface ActorFactory extends ActorSystemAware, ApplicationContextAware {

	public Optional<ActorRef> getLocalActor(String actorClass);
	
	public Optional<ActorRef> getLocalActor(String ref, String actorClass, int spinCount);
	
	public Optional<ActorRef> getLocalActorByRef(String ref);
	
}
