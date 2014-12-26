package com.sulaco.fuse.akka.actor;

import com.sulaco.fuse.akka.async.RequestSuspender;
import com.sulaco.fuse.akka.message.FuseInternalMessage;
import com.sulaco.fuse.akka.message.FuseReviveMessage;
import com.sulaco.fuse.akka.message.FuseSuspendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.UUID;

public class SuspendedAnimationActor extends FuseBaseActor {

    @Autowired
    RequestSuspender suspender;

    public SuspendedAnimationActor(ApplicationContext ctx) {
        super(ctx);
    }

    @Override
    public void onMessage(FuseInternalMessage message) {

        if (message instanceof FuseSuspendMessage) {
            suspender.suspend(message);
        }
        else
        if (message instanceof FuseReviveMessage) {

            FuseReviveMessage msg = (FuseReviveMessage) message;

            // attempt revival
            Optional<FuseInternalMessage> suspended = suspender.revive(msg.getId());
            suspended.ifPresent(
                sval -> {
                    // send suspended message back towards origin; add payload
                    //
                    sval.getContext()
                        .put(
                            "payload",
                            msg.getPayload()
                        );

                    sval.popOrigin()
                        .get()
                        .tell(
                            sval,
                            self()
                        );
                }
            );
        }
    }
}
