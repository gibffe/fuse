package com.sulaco.fuse.akka;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import java.util.Map;
import java.util.Optional;

import com.sulaco.fuse.config.route.Route;
import com.sulaco.fuse.config.route.RouteHandler;

public class FuseRequestMessageImpl implements FuseRequestMessage {
	
	ChannelHandlerContext channelContext;
	
	HttpRequest incomingRequest;
	
	FuseRequestContext requestContext;

	Route route;
	
	public void setRoute(Route route) {
		this.route = route;
	}
	
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
	public RouteHandler getHandler() {
		return route.getHandler();
	}

	@Override
	public Map<String, String> getParams() {
		return route.getParams();
	}

	@Override
	public Optional<String> getParam(String name) {
		return route.getParam(name);
	}

}
