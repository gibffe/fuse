package com.sulaco.fuse.akka.actor;

import io.netty.handler.codec.http.HttpMethod;

import java.util.Optional;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;




import com.sulaco.fuse.akka.message.FuseRequestMessage;
import com.sulaco.fuse.akka.message.FuseRequestMessageImpl;
import com.sulaco.fuse.config.route.Route;
import com.sulaco.fuse.config.route.RoutesConfig;


public class RouteFinderActor extends FuseEndpointActor {

	@Autowired protected RoutesConfig routes;
	
	public RouteFinderActor(ApplicationContext ctx) {
		super(ctx);
	}

	@Override
	protected void onRequest(final FuseRequestMessage message) {
		
		String uri = message.getRequest().getUri();
		
		Optional<Route> route = routes.getFuseRoute(uri);
		
		route.ifPresent(
		    rte -> {
		    	// add route to the message
				((FuseRequestMessageImpl) message).setRoute(rte);
				
				// pass message to handling actor
				rte.getHandler()
				   .getActor()
				   .ifPresent(
				       handler -> {
				    	   	  HttpMethod requested = message.getRequest().getMethod();
				    	   	  HttpMethod supported = rte.getHandler().getHttpMethod();
				    	   	  
				    	   	  if (supported.compareTo(requested) == 0) {
				    	   		  handler.tell(message, getSelf());  
				    	   	  }
				    	   	  else {
				    	   		  info(requested +" not supported by " + uri.toString());
				    	   		  unhandled(message);
				    	   	  }
				       }
				   );	
		    }
		);
	}

	public void setRoutes(RoutesConfig routes) {
		this.routes = routes;
	}
	
}
