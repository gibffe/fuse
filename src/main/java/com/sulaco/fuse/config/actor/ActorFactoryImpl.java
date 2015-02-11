package com.sulaco.fuse.config.actor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import akka.actor.ActorSelection;
import com.sulaco.fuse.akka.actor.FuseBaseActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.RoundRobinRouter;

@Component
public class ActorFactoryImpl implements ActorFactory {

    ActorSystem system;
    
    ApplicationContext ctx;
    
    // caches { actor_id -> actor instance }
    ConcurrentMap<String, ActorRef> cache;
    
    public ActorFactoryImpl() {
        this.cache = new ConcurrentHashMap<>();
    }
    
    @Override
    public Optional<ActorRef> getLocalActor(String actorClass) {
        return getLocalActor(actorClass, actorClass);
    }

    @Override
    public Optional<ActorRef> getLocalActor(String actorClass, String actorName) {
        
        ActorRef actorRef = null;
        
        try {
            final Class<?> clazz = validateClass(actorClass);
            actorRef = system.actorOf(Props.create(clazz),  actorName);
            init(actorRef);
        }
        catch (Exception ex) {
            log.error("Error creating actor:{}", actorClass, ex);   
        }
        
        return Optional.ofNullable(actorRef);
    }
    
    @Override
    public Optional<ActorRef> getLocalActorByRef(String ref) {
        return Optional.ofNullable(cache.get(ref));
    }
    
    @Override
    public Optional<ActorRef> getLocalActor(String ref, String actorClass, int spinCount) {
        return getLocalActor(ref, actorClass, ref, spinCount);
    }

    @Override
    public Optional<ActorRef> getLocalActor(String ref, String actorClass, String actorName, int spinCount) {
        
        ActorRef actorRef = null;
        
        try {
            // preconditions check
            final Class<?> clazz = validateClass(actorClass);

            if (spinCount < 0) {
                // never cache for negative spin, always return fresh actor shell
                actorRef = system.actorOf(Props.create(clazz), actorName);
            }
            else {
                actorRef 
                    = cache.computeIfAbsent(
                          ref, 
                          key -> {
                              if (spinCount == 1) {
                                  return system.actorOf(
                                      Props.create(clazz),
                                      actorName
                                  );
                              }
                              else {
                                  // return a router instead
                                  return system.actorOf(
                                      Props.empty()
                                          .withRouter(
                                              RoundRobinRouter.create(getRoutees(ref, clazz, spinCount))
                                          ),
                                      actorName
                                  );
                              }
                          }
                    );
            }
            init(actorRef);
        }
        catch (Exception ex) {
            log.error("Error creating actor: {}", actorClass, ex);
        }
        
        return Optional.ofNullable(actorRef);
    }
    
    public Iterable<ActorRef> getRoutees(String ref, Class<?> clazz, int spinCount) {
        
        List<ActorRef> actors = new ArrayList<>();

        ActorRef actorRef;
        for (int i = 0; i < spinCount; i++) {
            actorRef = system.actorOf(Props.create(clazz), ref + "_" + i);
            actors.add(init(actorRef));
        }
        
        return actors;
    }

    @Override
    public Optional<ActorSelection> select(String path) {
        try {
            return Optional.of(system.actorSelection(path));
        }
        catch (Exception ex) {
            log.error("Unable to select {} !", path);
        }

        return Optional.empty();
    }

    @Override
    public void setActorSystem(ActorSystem actorSystem) {
        this.system = actorSystem;
    }
    
    @Override
    public ActorSystem getActorSystem() {
        return this.system;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

    Class<?> validateClass(String actorClass) throws Exception {

        Class<?> clazz = Class.forName(actorClass);
        if (!FuseBaseActor.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(actorClass + " not an actor class !");
        }

        return clazz;
    }

    ActorRef init(ActorRef ref) {
        ref.tell(ctx, null);
        return ref;
    }
    
    private static final Logger log = LoggerFactory.getLogger(ActorFactoryImpl.class);

}
