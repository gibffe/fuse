package com.sulaco.fuse.config.route;

import io.netty.handler.codec.http.HttpMethod;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import akka.actor.ActorRef;

public class RouteHandler {
    
    Optional<ActorRef> actor;
    
    Optional<String>   methodName;
    
    HttpMethod httpMethod;
    
    
    private RouteHandler() {
        actor      = Optional.ofNullable(null);
        methodName = Optional.ofNullable(null);
        httpMethod = HttpMethod.GET;
    }

    public static RouteHandlerBuilder builder() {
        return new RouteHandlerBuilder();
    }
    
    public RouteHandler(Optional<ActorRef> actor, String methodName) {
        this.actor      = actor;
        this.methodName = Optional.ofNullable(methodName);
    }

    public Optional<ActorRef> getActor() {
        return actor;
    }

    public Optional<String> getMethodName() {
        return methodName;
    }
    
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }


    public static class RouteHandlerBuilder {
        
        private RouteHandler instance;
        
        public RouteHandlerBuilder() {
            this.instance = new RouteHandler();
        }
        
        public RouteHandlerBuilder withActorRef(Optional<ActorRef> actor) {
            instance.actor = actor;
            return this;
        }
        
        public RouteHandlerBuilder withMethodName(String methodName) {
            instance.methodName = Optional.ofNullable(methodName);
            return this;
        }
        
        public RouteHandlerBuilder withHttpMethod(String httpMethod) {
            if (!StringUtils.isEmpty(httpMethod)) {
                try {
                    instance.httpMethod = HttpMethod.valueOf(httpMethod);
                }
                catch (Exception ex) {
                    log.warn("Invalid method specified:{}, defaulting to GET", httpMethod, ex);
                    instance.httpMethod = HttpMethod.GET;
                }
            }
            return this;
        }
        
        public RouteHandler build() {
            return instance;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(RouteHandler.class);
    
}
