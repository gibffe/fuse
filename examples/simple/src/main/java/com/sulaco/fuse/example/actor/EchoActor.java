package com.sulaco.fuse.example.actor;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.akka.message.FuseRequestMessage;
import com.sulaco.fuse.example.tools.StringReverser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class EchoActor extends FuseEndpointActor {

    @Autowired
    StringReverser reverser;

    public EchoActor(ApplicationContext ctx) {
        super(ctx);
    }

    public void echo(FuseRequestMessage request) {

        Optional<String> param = request.getParam("param");

        proto.respond(
            request,
            param.orElseGet("not found"::toString) + "\n"
        );
    }

    public void reverse(FuseRequestMessage request) {

        Optional<String> param = request.getParam("param");

        proto.respond(
            request,
            reverser.reverse(param)
                    .orElseGet("not found"::toString) + "\n"
        );

    }
}
