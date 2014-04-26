package com.sulaco.fuse.akka.actor;

import static com.sulaco.fuse.util.Tools.lookupMethod;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SERVER;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.lang.reflect.Method;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;

import com.codahale.metrics.Timer;
import com.google.gson.Gson;
import com.sulaco.fuse.akka.FuseRequestMessage;
import com.sulaco.fuse.akka.syslog.SystemLogMessage;
import com.sulaco.fuse.akka.syslog.SystemLogMessage.LogLevel;
import com.sulaco.fuse.akka.syslog.SystemLogMessage.LogMessageBuilder;
import com.sulaco.fuse.config.route.RouteHandler;
import com.sulaco.fuse.metrics.MetricsRegistry;

public abstract class FuseActor extends UntypedActor {

	protected Gson gson;
	
	private ActorSelection logger;
		
	protected ApplicationContext ctx;
	
	protected Timer meter;
	
	@Autowired protected MetricsRegistry metrics;
	
	
	public FuseActor() {		
		this.gson = new Gson();
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

	protected void respond(FuseRequestMessage message, Object content) {
		this.respond(message, gson.toJson(content));
	}
	
	protected void respond(FuseRequestMessage message, String content) {
		this.respond(message, OK, content);
	}
	
	protected void respond(FuseRequestMessage message, HttpResponseStatus status, String content) {
		
		boolean keepAlive = isKeepAlive(message.getRequest());
        
		FullHttpResponse response 
			= new DefaultFullHttpResponse(
								HTTP_1_1, 
								status, 
								Unpooled.wrappedBuffer(content.getBytes())
			  );
		
		response.headers().set(SERVER         , VERSION);
        response.headers().set(CONTENT_TYPE   , APP_JSON);
        response.headers().set(CONTENT_LENGTH , response.content().readableBytes());

        if (!keepAlive) {
            message.getChannelContext()
            	   .write(response)
                   .addListener(ChannelFutureListener.CLOSE);
        } 
        else {
            response.headers()
                    .set(
                    		CONNECTION, 
                    		Values.KEEP_ALIVE
                    );
            
            message.getChannelContext()
                   .channel()
                   .write(response);
        }
        
        message.getChannelContext().flush();
	}

	@Override
	public void unhandled(Object message) {
		super.unhandled(message);
		if (message instanceof FuseRequestMessage) {
			respond((FuseRequestMessage) message, HttpResponseStatus.BAD_REQUEST, BAD_REQUEST);
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
	
	public static final String BAD_REQUEST = "{x_x}";
	public static final String APP_JSON = "application/json";
	public static final String VERSION  = "Fuse v0.0.1-SNAPSHOT";
	
	protected static final Logger log = LoggerFactory.getLogger(FuseActor.class);
}
