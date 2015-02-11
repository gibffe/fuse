package com.sulaco.fuse.akka.syslog;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import akka.actor.ActorPath;
import akka.actor.UntypedActor;

public class SystemLogActor extends UntypedActor {

    private ConcurrentMap<ActorPath, Logger> loggers;
    
    public SystemLogActor() {
        this.loggers = new ConcurrentHashMap<>();
    }
    
    @Override
    public void onReceive(Object message) throws Exception {
        
        if (message instanceof SystemLogMessage) {
            log((SystemLogMessage) message);            
        }
        else {
            unhandled(message);
        }
    }
    
    protected void log(SystemLogMessage message) {
        
        // find appropriate logger 
        Logger logger = findLogger(message.getOrigin());
        
        // log message
        switch (message.getLevel()) {
            case DEBUG: {
                logger.debug(message.getMessage().get(), message.getException().get());
            }
            case INFO : {
                logger.info(message.getMessage().get(), message.getException().get());
                break;
            }
            default: break;
        }
    }
    
    protected Logger findLogger(Optional<ActorPath> origin) {
        
        ActorPath path = origin.orElse(getContext().parent().path());
        
        return loggers.computeIfAbsent(
                                path, 
                                key -> {
                                    return LoggerFactory.getLogger(key.toString());
                                }
        );
    }

}
