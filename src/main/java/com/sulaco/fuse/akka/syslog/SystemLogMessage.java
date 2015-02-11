package com.sulaco.fuse.akka.syslog;

import java.util.Optional;

import akka.actor.ActorPath;

public class SystemLogMessage {
    
    LogLevel level;
    
    Optional<String> message;
    
    Optional<Throwable> exception;
    
    Optional<ActorPath> origin;
    
    private SystemLogMessage() { 
        
    }
    
    public static LogMessageBuilder builder() {
        return new LogMessageBuilder();
    }
    
    public static enum LogLevel {
        DEBUG, INFO, WARN, ERROR, FATAL
    }
    
    public static class LogMessageBuilder {
        
        private SystemLogMessage instance;
        
        public LogMessageBuilder() {
            this.instance = new SystemLogMessage();
        }
        
        public LogMessageBuilder withLevel(LogLevel level) {
            this.instance.level = level;
            return this;
        }
        
        public LogMessageBuilder withMessage(String msg) {
            instance.message = Optional.ofNullable(msg);
            return this;
        }
        
        public LogMessageBuilder withException(Throwable ex) {
            instance.exception = Optional.ofNullable(ex);
            return this;
        }
        
        public LogMessageBuilder withOrigin(ActorPath origin) {
            instance.origin = Optional.ofNullable(origin);
            return this;
        }
        
        public SystemLogMessage build() {
            
            if (instance.level == null) {
                instance.level = LogLevel.DEBUG;
            }
            
            if (instance.message == null) {
                instance.message = Optional.ofNullable(null);
            }
            
            if (instance.exception == null) {
                instance.exception = Optional.ofNullable(null);
            }
            
            if (instance.origin == null) {
                instance.origin = Optional.ofNullable(null);
            }
            
            //
            return instance;
        }
    }

    public LogLevel getLevel() {
        return level;
    }

    public Optional<String> getMessage() {
        return message;
    }

    public Optional<Throwable> getException() {
        return exception;
    }

    public Optional<ActorPath> getOrigin() {
        return origin;
    }

}
