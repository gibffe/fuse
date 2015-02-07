package com.sulaco.fuse.example.endpoint;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.akka.message.FuseRequestMessage;
import com.sulaco.fuse.config.annotation.FuseEndpoint;

@FuseEndpoint(path = "/echo/<param1>")
public class EchoParam extends FuseEndpointActor {

    @Override
    protected void onRequest(FuseRequestMessage request) {
        proto.respond(request, request.getParam("param1").get());
    }
}
