package com.sulaco.fuse.example.actor;

import com.sulaco.fuse.akka.actor.FuseBaseActor;
import com.sulaco.fuse.akka.message.FuseInternalMessage;
import com.sulaco.fuse.config.annotation.FuseActor;
import com.sulaco.fuse.example.service.EchoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@FuseActor(id = "echo")
public class Echo extends FuseBaseActor {

    @Autowired EchoService echoService;

    @Override
    protected void onInternal(FuseInternalMessage message) {

        Optional<String> echo = message.getContext().get("echo");

        echo.ifPresent(
            value -> {
                message.getContext().put("echo", echoService.echo(value));
                bubble(message); // send message back where it came from
            }
        );
    }
}
