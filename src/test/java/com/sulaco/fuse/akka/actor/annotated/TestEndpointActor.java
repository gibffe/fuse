package com.sulaco.fuse.akka.actor.annotated;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.config.annotation.FuseEndpoint;
import org.springframework.context.ApplicationContext;

@FuseEndpoint( path ="/test", method = "GET")
public class TestEndpointActor extends FuseEndpointActor {

}
