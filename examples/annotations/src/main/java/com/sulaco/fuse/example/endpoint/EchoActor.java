package com.sulaco.fuse.example.endpoint;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.akka.message.FuseInternalMessage;
import com.sulaco.fuse.akka.message.FuseMessageContext;
import com.sulaco.fuse.akka.message.FuseRequestMessage;
import com.sulaco.fuse.config.annotation.FuseEndpoint;

@FuseEndpoint( path = "/echo" )
public class EchoActor extends FuseEndpointActor {

    @Override
    protected void onRequest(FuseRequestMessage request) {

        // we'll let the Echo actor figure out how to echo the request uri string
        //
        FuseInternalMessage message = newMessage(request);

        message.getContext()
               .put(
                   "echo",
                   request.getRequest().getUri()
               );

        send(message, "/user/echo");

        // once the message is processed by example.actor.Echo, at some point in the future this actor will receive an
        // internal message back
    }

    @Override
    protected void onInternal(FuseInternalMessage message) {

        FuseMessageContext ctx = message.getContext();

        ctx.getRequest().ifPresent(
            req -> {
                proto.respond(req, ctx.get("echo"));
            }
        );
    }
}
