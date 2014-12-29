package com.sulaco.fuse.netty;


import akka.testkit.TestActorRef;
import com.sulaco.fuse.ActorAwareTest;
import com.sulaco.fuse.akka.actor.RouteFinderActor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnit44Runner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnit44Runner.class)
public class FuseChannelHandlerTest extends ActorAwareTest {

    FuseChannelHandler instance;

    TestActorRef<RouteFinderActor> mockRouter;
    @Mock ChannelHandlerContext mockCtx;


    @Before
    public void setup() {
        super.setup();

        mockRouter = mockEndpoint(RouteFinderActor.class);

        instance = new FuseChannelHandler(mockRouter);
    }

    @Test
    public void testCreate() {
        // then
        assertThat(instance).isNotNull();
        assertThat(instance.router).isNotNull();
    }

    @Test
    public void testChannelReadHttpRequest() throws Exception {
        // given
        Object mockRequest = mock(HttpRequest.class);

        // when
        instance.channelRead(mockCtx, mockRequest);
    }

    @Test
    public void testChannelReadOther() throws Exception {
        // given
        Object mockRequest = mock(Object.class);

        // when
        instance.channelRead(mockCtx, mockRequest);
    }

    @Test
    public void testExceptionCaught() throws Exception {
        // when
        instance.exceptionCaught(mockCtx, new Exception("foo"));
        // then
        verify(mockCtx, times(1)).close();
    }

    @Test
    public void testSetRouter() {

        instance.setRouter(null);
        assertThat(instance.router).isNull();

        instance.setRouter(mockRouter);
        assertThat(instance.router).isNotNull();
    }
}
