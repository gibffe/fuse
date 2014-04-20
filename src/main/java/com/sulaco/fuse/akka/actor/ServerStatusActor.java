package com.sulaco.fuse.akka.actor;

import org.springframework.context.ApplicationContext;

import com.sulaco.fuse.akka.FuseRequestMessage;

public class ServerStatusActor extends FuseActor {

	public ServerStatusActor(ApplicationContext ctx) {
		super(ctx);
	}

	protected void serverStatus(FuseRequestMessage message) {

	}

	protected void vmStatus(FuseRequestMessage message) {
		
	}
	
}
