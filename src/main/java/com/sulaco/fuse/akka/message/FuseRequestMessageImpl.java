package com.sulaco.fuse.akka.message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Optional;

import com.sulaco.fuse.config.route.Route;
import com.sulaco.fuse.config.route.RouteHandler;

public class FuseRequestMessageImpl implements FuseRequestMessage {
	
	ChannelHandlerContext channelContext;
	
	HttpRequest incomingRequest;
	
	Route route;
	
	volatile boolean flushed = false;
	
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
	
	@Override
	public String getRequestBody() {
		return ((DefaultFullHttpRequest) incomingRequest).content().toString(charset_utf8);
	}

	public void setRoute(Route route) {
		this.route = route;
	}
	
	@Override
	public void flush() {
		channelContext.flush();
		flushed = true;
	}

	@Override
	public boolean flushed() {
		return flushed;
	}

	private static final Charset charset_utf8 = Charset.forName("UTF-8");
	
}
