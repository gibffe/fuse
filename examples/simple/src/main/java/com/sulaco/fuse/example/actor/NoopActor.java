package com.sulaco.fuse.example.actor;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.akka.message.FuseRequestMessage;
import org.springframework.context.ApplicationContext;

public class NoopActor extends FuseEndpointActor {

    public NoopActor(ApplicationContext ctx) {
        super(ctx);
    }

    @Override
    protected void onRequest(FuseRequestMessage request) {
        proto.ok(request);
    }
}
