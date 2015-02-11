package com.sulaco.fuse.akka.message;

import java.util.Deque;
import java.util.Optional;

import akka.actor.ActorRef;

public interface FuseOriginChain {

    Optional<ActorRef> popOrigin();
    
    void pushOrigin(ActorRef actorRef);

    Deque<ActorRef> getChain();
}
