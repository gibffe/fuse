package com.sulaco.fuse.config.actor;

import java.util.Optional;

import akka.actor.ActorSelection;
import org.springframework.context.ApplicationContextAware;

import akka.actor.ActorRef;

import com.sulaco.fuse.akka.actor.ActorSystemAware;

public interface ActorFactory extends ActorSystemAware, ApplicationContextAware {

	public Optional<ActorRef> getLocalActor(String actorClass);
	
	public Optional<ActorRef> getLocalActor(String actorClass, String actorName);
	
	public Optional<ActorRef> getLocalActor(String ref, String actorClass, int spinCount);
	
	public Optional<ActorRef> getLocalActor(String ref, String actorClass, String actorName, int spinCount);
	
	public Optional<ActorRef> getLocalActorByRef(String ref);

    public Optional<ActorSelection> select(String path);

	
}
