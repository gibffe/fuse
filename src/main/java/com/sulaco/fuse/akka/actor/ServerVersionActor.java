package com.sulaco.fuse.akka.actor;

import org.springframework.context.ApplicationContext;

import com.sulaco.fuse.akka.FuseRequestMessage;

public class ServerVersionActor extends FuseActor {

	private static final String ECHO = "{ \"version\" : \"" + VERSION + "\" }";
	
	public ServerVersionActor(ApplicationContext ctx) {
		super(ctx);
	}
	
	@Override
	protected void onReceive(FuseRequestMessage message) {
		respond(message, ECHO);
	}

}
