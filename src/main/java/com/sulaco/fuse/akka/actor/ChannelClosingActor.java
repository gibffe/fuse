package com.sulaco.fuse.akka.actor;


import com.sulaco.fuse.akka.message.FuseInternalMessage;
import org.springframework.context.ApplicationContext;

public class ChannelClosingActor extends FuseBaseActor {

    @Override
    public void onMessage(FuseInternalMessage message) {
        message.getContext()
               .getRequest()
               .ifPresent(
                   req -> {
                       req.getChannelContext().close();
                   }
               );
    }
}
