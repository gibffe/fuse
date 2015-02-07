package com.sulaco.fuse.example.actor;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.akka.message.FuseInternalMessageImpl;
import com.sulaco.fuse.akka.message.FuseRequestMessage;
import org.springframework.context.ApplicationContext;

public class VoidActor extends FuseEndpointActor {

    @Override
    protected void onRequest(FuseRequestMessage request) {
        send(
            newMessage(request),
            "/user/ChannelReaper"
        );
    }
}
