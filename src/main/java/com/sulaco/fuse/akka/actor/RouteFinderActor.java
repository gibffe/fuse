package com.sulaco.fuse.akka.actor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.sulaco.fuse.akka.FuseRequestMessage;
import com.sulaco.fuse.akka.FuseRequestMessageImpl;
import com.sulaco.fuse.config.route.Route;
import com.sulaco.fuse.config.route.RoutesConfig;


public class RouteFinderActor extends FuseActor {

	@Autowired protected RoutesConfig routes;
	
	public RouteFinderActor(ApplicationContext ctx) {
		super(ctx);
	}

	@Override
	protected void onReceive(final FuseRequestMessage message) {
		
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
				         	  handler.tell(message, getSelf());
				       }
				   );	
		    }
		);
	}

	public void setRoutes(RoutesConfig routes) {
		this.routes = routes;
	}
	
}
