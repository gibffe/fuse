package com.sulaco.fuse.config.actor;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sulaco.fuse.akka.actor.NoopActor;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import org.springframework.context.ApplicationContext;

@RunWith(MockitoJUnitRunner.class)
public class ActorFactoryImplTest {

    ActorFactoryImpl instance;
    
    ActorSystem system;

    @Mock ApplicationContext mockCtx;
    
    @Before
    public void setup() {
        
        system = spy(ActorSystem.create());
        
        instance = new ActorFactoryImpl();
        instance.setActorSystem(system);
        instance.setApplicationContext(mockCtx);
    }
    
    @Test
    public void testCreateSimpleActor() {
        
        // when
        Optional<ActorRef> result = instance.getLocalActor("foo", "com.sulaco.fuse.akka.actor.NoopActor", 1);
        
        // then
        assertThat(result.isPresent()).isTrue();
        
        assertThat(instance.cache.containsKey("foo"));
        assertThat(instance.getLocalActorByRef("foo").isPresent()).isTrue();
        
        verify(system, times(1)).actorOf(any(Props.class), anyString());
    }
    
    @Test
    public void testCreateRouterActor() {
        
        // when
        Optional<ActorRef> result = instance.getLocalActor("foo", "com.sulaco.fuse.akka.actor.NoopActor", 2);
        
        // then
        assertThat(result.isPresent()).isTrue();
    
        assertThat(instance.cache.containsKey("foo"));
        assertThat(instance.getLocalActorByRef("foo").isPresent()).isTrue();

        verify(system, times(3)).actorOf(any(Props.class), anyString());
    }

}
