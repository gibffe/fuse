package com.sulaco.fuse;

import akka.actor.Actor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import com.codahale.metrics.Timer;
import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.codec.WireProtocol;
import org.junit.Ignore;
import org.mockito.Mock;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.when;

@Ignore
public class ActorAwareTest {

    ActorSystem system = ActorSystem.create("test");

    @Mock protected ApplicationContext mockAppCtx;

    @Mock protected AutowireCapableBeanFactory mockAutowireFactory;

    @Mock protected WireProtocol mockProto;

    @Mock protected Timer mockMeter;

    protected void setup() {
        when(mockAppCtx.getAutowireCapableBeanFactory()).thenReturn(mockAutowireFactory);
    }

    protected <T extends FuseEndpointActor> TestActorRef<T> mockEndpoint(Class<T> clazz) {
        Props props = Props.create(clazz, mockAppCtx);
        TestActorRef<T> testActorRef = TestActorRef.create(system, props);
        testActorRef.underlyingActor().setProto(mockProto);
        //testActorRef.underlyingActor().setMeter(mockMeter);

        return testActorRef;
    }

}
