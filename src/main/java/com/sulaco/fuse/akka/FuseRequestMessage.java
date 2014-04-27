package com.sulaco.fuse.akka;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import com.sulaco.fuse.config.route.Route;

public interface FuseRequestMessage extends Route {

	ChannelHandlerContext getChannelContext();
	
	HttpRequest getRequest();
	
	FuseRequestContext getContext();
	
	String getRequestBody();
	
}
