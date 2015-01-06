package com.sulaco.fuse.akka.actor;

import static com.sulaco.fuse.util.Tools.lookupMethod;

import java.lang.reflect.Method;
import java.util.Optional;

import akka.actor.ActorSelection;
import com.sulaco.fuse.akka.message.FuseSuspendMessage;
import com.sulaco.fuse.akka.message.FuseSuspendMessageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.codahale.metrics.Timer;
import com.sulaco.fuse.akka.message.FuseRequestMessage;
import com.sulaco.fuse.codec.WireProtocol;
import com.sulaco.fuse.config.route.RouteHandler;
import com.sulaco.fuse.metrics.MetricsRegistry;

public abstract class FuseEndpointActor extends FuseBaseActor {
	
	protected Timer meter;

	@Autowired protected MetricsRegistry metrics;
	
	public FuseEndpointActor() {
		super();
	}
	
	public FuseEndpointActor(ApplicationContext ctx) {
		super(ctx);
		
		if (ctx != null) {
            if (metrics != null) {
                meter = metrics.getRegistry().timer(getClass().getName());
            }
		}
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		Timer.Context context = null;
		try {
			context = meter.time();
			
			if (message instanceof FuseRequestMessage) {
				onRequest((FuseRequestMessage) message);
			}
			else {
				super.onReceive(message);
			}
		}
		catch (Exception ex) {
			log.error("Error handling request !", ex);
			unhandled(message);
		}
		finally {
			if (context != null) {
				context.stop();
			}
		}
	}
	
	protected void onRequest(final FuseRequestMessage request) {
		
		RouteHandler rhandler = request.getHandler();
		
		Optional<String> method = rhandler.getMethodName();
		
		if (method.isPresent()) {
			// Invoke configured method instead of default 'onReceive'. Method needs to have correct
			// signature, otherwise, fallback to 'onReceive' will occur.
			//
			Optional<Method> target = lookupMethod(this, method.get());
			if (target.isPresent()) {
				try {
					target.get()
						  .invoke(this, request);
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

    public void setProto(WireProtocol proto) {
        this.proto = proto;
    }

    public void setMeter(Timer meter) {
        this.meter = meter;
    }

	protected static final Logger log = LoggerFactory.getLogger(FuseEndpointActor.class);
}
