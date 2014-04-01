package com.sulaco.fuse.akka.actor;

import org.springframework.context.ApplicationContext;

import com.sulaco.fuse.akka.FuseRequestMessage;

public class ServerStatusActor extends FuseActor {

	public ServerStatusActor(ApplicationContext ctx) {
		super(ctx);
	}

	@Override
	protected void onReceive(FuseRequestMessage message) {

	}
	
}
