package com.sulaco.fuse.akka.actor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.sulaco.fuse.FuseVersion;
import com.sulaco.fuse.akka.FuseRequestMessage;

public class ServerVersionActor extends FuseActor {

	@Autowired FuseVersion version;
	
	public ServerVersionActor(ApplicationContext ctx) {
		super(ctx);
	}
	
	@Override
	protected void onReceive(FuseRequestMessage message) {
		proto.respond(message, version);
	}

}
