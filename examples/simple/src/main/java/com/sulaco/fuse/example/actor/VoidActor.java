package com.sulaco.fuse.example.actor;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.akka.message.FuseInternalMessageImpl;
import com.sulaco.fuse.akka.message.FuseRequestMessage;
import org.springframework.context.ApplicationContext;

/**
 * Created by llech on 12/28/14.
 */
public class VoidActor extends FuseEndpointActor {

    public VoidActor(ApplicationContext ctx) {
        super(ctx);
    }

    @Override
    protected void onRequest(FuseRequestMessage request) {
        getContext().actorSelection("/user/ChannelReaper")
                    .tell(
                        new FuseInternalMessageImpl(request)
                    );
    }
}
