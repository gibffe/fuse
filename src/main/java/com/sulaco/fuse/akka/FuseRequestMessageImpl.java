package com.sulaco.fuse.akka;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import java.io.Serializable;

import com.sulaco.fuse.config.route.Route;

public class FuseRequestMessageImpl implements FuseRequestMessage, Serializable {
	
	ChannelHandlerContext channelContext;
	
	HttpRequest incomingRequest;
	
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
	public HttpRequest getIncomingRequest() {
		return incomingRequest;
	}

	@Override
	public Route getRoute() {
		return route;
	}
	
	private static final long serialVersionUID = -716541497905060639L;

}
