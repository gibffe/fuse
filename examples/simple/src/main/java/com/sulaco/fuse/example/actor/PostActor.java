package com.sulaco.fuse.example.actor;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.akka.message.FuseRequestMessage;
import org.springframework.context.ApplicationContext;

public class PostActor extends FuseEndpointActor {

    @Override
    protected void onRequest(FuseRequestMessage request) {

        proto.respond(request, request.getRequestBody()+"\n");
    }
}
