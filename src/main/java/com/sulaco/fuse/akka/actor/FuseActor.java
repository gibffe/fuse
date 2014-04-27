package com.sulaco.fuse.akka.actor;

import static com.sulaco.fuse.util.Tools.lookupMethod;

import java.lang.reflect.Method;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

import com.codahale.metrics.Timer;
import com.sulaco.fuse.akka.FuseRequestMessage;
import com.sulaco.fuse.akka.syslog.SystemLogMessage;
import com.sulaco.fuse.akka.syslog.SystemLogMessage.LogLevel;
import com.sulaco.fuse.akka.syslog.SystemLogMessage.LogMessageBuilder;
import com.sulaco.fuse.codec.WireProtocol;
import com.sulaco.fuse.config.route.RouteHandler;
import com.sulaco.fuse.metrics.MetricsRegistry;

public abstract class FuseActor extends UntypedActor {

	private ActorSelection logger;
		
	protected ApplicationContext ctx;
	
	protected Timer meter;
	
	@Autowired protected MetricsRegistry metrics;
	
	@Autowired protected WireProtocol proto;
	
	
	public FuseActor() {		
		this.logger = getContext().actorSelection("/user/logger");
	}
	
	public FuseActor(ApplicationContext ctx) {
		this();
		this.ctx = ctx;
		
		if (ctx != null) {
			ctx.getAutowireCapableBeanFactory()
			   .autowireBean(this);
			
			meter = metrics.getRegistry().timer(getClass().getName());
		}
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		Timer.Context context = null;
		try {
			context = meter.time();
			
			if (message instanceof FuseRequestMessage) {
				onReceive((FuseRequestMessage) message);
			}
			else {
				unhandled(message);
			}
		}
		catch (Exception ex) {
			unhandled(message);
		}
		finally {
			if (context != null) {
				context.stop();
			}
		}
	}
	
	protected void onReceive(final FuseRequestMessage message) {
		
		RouteHandler rhandler = ((FuseRequestMessage) message).getHandler();
		
		Optional<String> method = rhandler.getMethodName();
		
		if (method.isPresent()) {
			// Invoke configured method instead of default 'onReceive'. Method needs to have correct
			// signature, otherwise, fallback to 'onReceive' will occur.
			//
			Optional<Method> target = lookupMethod(this, method.get());
			if (target.isPresent()) {
				try {
					target.get()
						  .invoke(this, message);
				}
				catch (Exception ex) {
					log.warn("[fuse] Invocation failure. x_x", ex);
				}
			}		
		}
		else {
			log.warn("[fuse] No handling method specified. Override onReceive. x_x");
		}
	}
	
	@Override
	public void unhandled(Object message) {
		super.unhandled(message);
		if (message instanceof FuseRequestMessage) {
			proto.error((FuseRequestMessage) message);
		}
	}

	protected void info(String message) {
		
		LogMessageBuilder builder = SystemLogMessage.builder();
		
		SystemLogMessage logmessage 
			= builder.withLevel(LogLevel.INFO)
					 .withMessage(message)
					 .build();
		
		logger.tell(logmessage, getSelf());
	}

	
	protected static final Logger log = LoggerFactory.getLogger(FuseActor.class);
}
