package com.sulaco.fuse.config.actor;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
	
	ConcurrentMap<String, ActorRef> cache;
	
	public ActorFactoryImpl() {
		this.cache = new ConcurrentHashMap<>();
	}
	
	@Override
	public ActorRef getLocalActor(String actorClass, int spinCount) {
		
		try {
			// preconditions check
			final Class<?> clazz = Class.forName(actorClass);
			clazz.getConstructor(ApplicationContext.class);

			if (spinCount < 0) {
				// never cache for negative spin, always return fresh actor shell
				return system.actorOf(Props.create(clazz, ctx),	actorClass);		
			}
			else {
				return cache.computeIfAbsent(
							actorClass, 
							key -> {
								if (spinCount == 1) {
									return system.actorOf(
												Props.create(clazz, ctx), 
												actorClass
									);
								}
								else {
									// return a router instead
									return system.actorOf(
												Props.empty()
												 	 .withRouter(
												         RoundRobinRouter.create(routees(clazz, spinCount))
												     )
									);
								}
							}
				);
			}
		}
		catch (Exception ex) {
			log.error("Error creating actor:{}", actorClass, ex);
		}
		//
		return null;
	}
	
	protected Iterable<ActorRef> routees(Class<?> clazz, int spinCount) {
		
		List<ActorRef> actors = Collections.nCopies(spinCount, null);
		
		actors.stream()
	          .map(
	              e -> {
	                  return system.actorOf(
				    			      Props.create(clazz, ctx),
				    				  clazz.getName()
	    			  );
	              }
	          );
		
		return actors;
	}

	@Override
	public void setActorSystem(ActorSystem actorSystem) {
		this.system = actorSystem;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}
	
	private static final Logger log = LoggerFactory.getLogger(ActorFactoryImpl.class);
}
