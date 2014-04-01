package com.sulaco.fuse.akka.actor;

import org.springframework.context.ApplicationContext;

import com.sulaco.fuse.akka.FuseRequestMessage;

public class NoopActor extends FuseActor {

	public NoopActor(ApplicationContext ctx) {
		super(ctx);
	}

	@Override
	protected void onReceive(FuseRequestMessage message) {
		
	}

}
