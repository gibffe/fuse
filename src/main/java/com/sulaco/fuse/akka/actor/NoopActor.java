package com.sulaco.fuse.akka.actor;

import org.springframework.context.ApplicationContext;

import com.sulaco.fuse.akka.message.FuseRequestMessage;

public class NoopActor extends FuseEndpointActor {

	public NoopActor(ApplicationContext ctx) {
		super(ctx);
	}

	@Override
	protected void onRequest(FuseRequestMessage message) {
		
	}

}
