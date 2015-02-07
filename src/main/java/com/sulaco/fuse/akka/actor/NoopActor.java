package com.sulaco.fuse.akka.actor;

import com.sulaco.fuse.akka.message.FuseRequestMessage;

public class NoopActor extends FuseEndpointActor {

	@Override
	protected void onRequest(FuseRequestMessage message) {
		
	}

}
