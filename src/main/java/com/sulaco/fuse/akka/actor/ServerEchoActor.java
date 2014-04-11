package com.sulaco.fuse.akka.actor;

import org.springframework.context.ApplicationContext;

import com.sulaco.fuse.akka.FuseRequestMessage;

@SuppressWarnings("unused")
public class ServerEchoActor extends FuseActor {

	public ServerEchoActor(ApplicationContext ctx) {
		super(ctx);
	}
	
	@Override
	protected void onReceive(FuseRequestMessage message) {
		int a = 3;
	}

}
