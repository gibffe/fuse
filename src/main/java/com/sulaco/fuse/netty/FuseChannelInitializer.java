package com.sulaco.fuse.netty;

import com.sulaco.fuse.config.ConfigSource;
import com.typesafe.config.Config;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import akka.actor.ActorRef;

@Component
public class FuseChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired ConfigSource configSource;

    protected List<ChannelHandler> channelHandlers;

    protected ActorRef router;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();

        // add handlers stack 
        //
        if (channelHandlers != null) {
            for (ChannelHandler handler : channelHandlers) {
                pipeline.addLast(handler.getClass().getName(), handler);
            }
        }
        else {
            addDefaultHandlerStack(pipeline);
        }
        
        // at last , add FUSE channel handler, passing the router actor reference
        //
        pipeline.addLast("handler", new FuseChannelHandler(this.router));
    }

    protected void addDefaultHandlerStack(ChannelPipeline pipeline) {
        Config config = configSource.getConfig();

        pipeline.addLast("decoder"       , new HttpRequestDecoder());
        pipeline.addLast("aggregator"    , new HttpObjectAggregator(66560));
        pipeline.addLast("encoder"       , new HttpResponseEncoder());
        pipeline.addLast("chunkedWriter" , new ChunkedWriteHandler());
        pipeline.addLast("idlehandler"   , new IdleStateHandler(
                                               config.getLong("netty.reader.idle.time"),
                                               config.getLong("netty.writer.idle.time"),
                                               config.getLong("netty.either.idle.time"),
                                               TimeUnit.MILLISECONDS
                                           ));
    }

    public void setChannelHandlers(List<ChannelHandler> channelHandlers) {
        this.channelHandlers = channelHandlers;
    }

    public void setRouter(ActorRef router) {
        this.router = router;
    }

}
