package com.sulaco.fuse.config.route;

import akka.actor.ActorRef;

import java.util.Optional;

public interface RoutesConfig {

    public void parse();
    
    public Optional<Route> getFuseRoute(String uri);

    public void addEndpoint(ActorRef ref, String httpMethod, String path);
}
