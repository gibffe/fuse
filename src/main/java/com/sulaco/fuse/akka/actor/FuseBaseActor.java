package com.sulaco.fuse.akka.actor;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import com.sulaco.fuse.akka.message.*;
import com.sulaco.fuse.codec.WireProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

import com.sulaco.fuse.akka.syslog.SystemLogMessage;
import com.sulaco.fuse.akka.syslog.SystemLogMessage.LogLevel;
import com.sulaco.fuse.akka.syslog.SystemLogMessage.LogMessageBuilder;

public abstract class FuseBaseActor extends UntypedActor {

    protected ActorSelection logger;

    protected ActorSelection animator;

    protected ApplicationContext ctx;

    @Autowired protected WireProtocol proto;
    
    public FuseBaseActor() {
        this.logger   = getContext().actorSelection("/user/logger");
        this.animator = getContext().actorSelection("/user/animator");
    }
    
    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof FuseInternalMessage) {
            onMessage((FuseInternalMessage) message);
        }
        else if (message instanceof ApplicationContext) {
            autowire((ApplicationContext) message);
        }
        else {
            unhandled(message);
        }
    }

    public void autowire(ApplicationContext ctx) {
        this.ctx = ctx;
        try {
            if (ctx != null) {
                ctx.getAutowireCapableBeanFactory()
                   .autowireBean(this);
            }
        }
        catch (Exception ex) {
            // TODO: log
        }
    }
    
    void onMessage(FuseInternalMessage message) {

        if (message instanceof FuseSuspendMessage) {
            Optional<Object> payload = message.getContext().get("payload");
            onRevive(message, payload);
        }
        else {
            onInternal(message);
        }
    }

    protected void onRevive(FuseInternalMessage message, Optional<?> payload) {
        FuseRequestMessage request = message.getContext().getRequest().get();
        proto.respond(request, payload.get());
    }

    protected void onInternal(FuseInternalMessage message) {
        bubble(message);
    }

    protected final void bubble(FuseInternalMessage message, Optional<?> payload) {

        FuseInternalMessage msg = message;
        if (message instanceof FuseSuspendMessage) {
            msg = new FuseInternalMessageImpl(message);
        }

        msg.getContext()
           .put(
               "payload",
               payload
           );

        bubble(msg);
    }

    protected final void bubble(FuseInternalMessage message) {
        Optional<ActorRef> origin = message.popOrigin();

        // push message down the chain
        if (origin.isPresent()) {
            origin.get()
                  .tell(
                      message,
                      self()
                  );
        }
        else {
            // no more actors that could potentially handle this internal message, this
            // would usually be a logic error
            unhandled(message);
        }
    }

    protected void send(FuseInternalMessage message, String path) {
        send(message, getContext().actorSelection(path));
    }

    protected void send(FuseInternalMessage message, ActorSelection selection) {
        message.pushOrigin(self());
        selection.tell(message, self());
    }

    protected void bounce(FuseInternalMessage message, ActorSelection selection, String bouncePath) {
        ActorRef bounceTo = getContext().actorFor(bouncePath);
        message.pushOrigin(bounceTo);
        selection.tell(message, bounceTo);
    }

    @Override
    public void unhandled(Object message) {
        super.unhandled(message);
    }
    
    protected FuseInternalMessage newMessage(FuseRequestMessage request) {
        
        FuseInternalMessage message = new FuseInternalMessageImpl();
        message.getContext().setRequest(request);
        
        return message;
    }

    protected FuseInternalMessage newMessage(FuseInternalMessage internal) {
        return new FuseInternalMessageImpl(internal);
    }
    
    protected void info(String message) {
        
        LogMessageBuilder builder = SystemLogMessage.builder();
        
        SystemLogMessage logmessage 
            = builder.withLevel(LogLevel.INFO)
                     .withMessage(message)
                     .build();
        
        logger.tell(logmessage, getSelf());
    }

    protected void suspend(FuseInternalMessage message) {
        send(
            new FuseSuspendMessageImpl(message),
            animator
        );
    }

    protected void suspend(FuseRequestMessage message) {
        send(
            new FuseSuspendMessageImpl(message),
            animator
        );
    }

    protected void suspend(FuseRequestMessage message, String bouncePath) {
        bounce(
            new FuseSuspendMessageImpl(message),
            animator,
            bouncePath
        );
    }

    protected Object revive(FuseInternalMessage message, Optional<?> payload) {
        send(
            new FuseReviveMessageImpl(message.getRequestId(), payload),
            animator
        );
        return null;
    }

    protected Object revive(FuseRequestMessage request, Optional<?> payload) {
        send(
            new FuseReviveMessageImpl(request.getId(), payload),
            animator
        );
        return null;
    }

}
