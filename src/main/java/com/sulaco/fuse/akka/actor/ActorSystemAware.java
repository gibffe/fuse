package com.sulaco.fuse.akka.actor;

import akka.actor.ActorSystem;

public interface ActorSystemAware {

	public void setActorSystem(ActorSystem actorSystem);
}
