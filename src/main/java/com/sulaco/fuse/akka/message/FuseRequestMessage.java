package com.sulaco.fuse.akka.message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import com.sulaco.fuse.config.route.Route;

public interface FuseRequestMessage extends Route {

	ChannelHandlerContext getChannelContext();
	
	HttpRequest getRequest();
		
	String getRequestBody();
	
	void flush();
	
	boolean flushed();
	
}
