package com.sulaco.fuse.netty;

import com.sulaco.fuse.util.IdSource;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import akka.actor.ActorRef;

import com.sulaco.fuse.akka.message.FuseRequestMessageImpl;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import static com.sulaco.fuse.util.IdSource.*;

/**
 * Incoming requests are handled by a router actor - it will use a fixed pool of
 * child actors that will match the rest pattern to a preconfigured handling actor.
 * 
 * @author gibffe
 *
 */
public class FuseChannelHandler extends ChannelInboundHandlerAdapter {
      
    protected ActorRef router;

    public FuseChannelHandler(ActorRef router) {
        super();
        this.router = router;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        //ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {

            router.tell(
                new FuseRequestMessageImpl(IdSource.getLong(), ctx, (HttpRequest) msg),
                null
            );
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.ALL_IDLE) {
                ctx.close();
                return;
            }
            if (event.state() == IdleState.READER_IDLE) {
                ctx.close();
                return;
            }
            if (event.state() == IdleState.WRITER_IDLE) {
                ctx.close();
                return;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    public void setRouter(ActorRef router) {
        this.router = router;
    }
    
}
