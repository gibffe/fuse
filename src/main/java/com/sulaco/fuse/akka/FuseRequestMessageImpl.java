package com.sulaco.fuse.akka;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import java.io.Serializable;

import com.sulaco.fuse.config.route.Route;

public class FuseRequestMessageImpl implements FuseRequestMessage {
	
	ChannelHandlerContext channelContext;
	
	HttpRequest incomingRequest;
	
	FuseRequestContext requestContext;

	Route route;
	
	
	public FuseRequestMessageImpl(ChannelHandlerContext context, HttpRequest request) {
		this.channelContext  = context;
		this.incomingRequest = request;
	}

	@Override
	public ChannelHandlerContext getChannelContext() {
		return channelContext;
	}

	@Override
	public HttpRequest getRequest() {
		return incomingRequest;
	}

	@Override
	public FuseRequestContext getContext() {
		return requestContext;
	}
	
	@Override
	public Route getRoute() {
		return route;
	}

}
