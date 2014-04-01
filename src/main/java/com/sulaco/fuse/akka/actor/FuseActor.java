package com.sulaco.fuse.akka.actor;

import org.springframework.context.ApplicationContext;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

import com.sulaco.fuse.akka.FuseRequestMessage;
import com.sulaco.fuse.akka.syslog.SystemLogMessage;
import com.sulaco.fuse.akka.syslog.SystemLogMessage.LogLevel;
import com.sulaco.fuse.akka.syslog.SystemLogMessage.LogMessageBuilder;

public abstract class FuseActor extends UntypedActor {

	private ActorSelection logger;
	
	protected ApplicationContext ctx;
	
	public FuseActor(ApplicationContext ctx) {
		this.ctx = ctx;
		this.logger = getContext().actorSelection("/user/logger");
	}
	
	@Override
	public void onReceive(Object message) throws Exception {

		if (message instanceof FuseRequestMessage) {
			onReceive((FuseRequestMessage) message);
		}
		else {
			unhandled(message);
		}
		
	}
	
	protected abstract void onReceive(FuseRequestMessage message);
	
	protected void info(String message) {
		
		LogMessageBuilder builder = SystemLogMessage.builder();
		
		SystemLogMessage logmessage 
			= builder.withLevel(LogLevel.INFO)
					 .withMessage(message)
					 .build();
		
		logger.tell(logmessage, getSelf());
	}
}
