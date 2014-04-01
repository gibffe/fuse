package com.sulaco.fuse.akka.actor;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.sulaco.fuse.akka.FuseRequestMessage;
import com.sulaco.fuse.config.route.Route;
import com.sulaco.fuse.config.route.RoutesConfig;


public class RouteFinderActor extends FuseActor {

	@Autowired protected RoutesConfig routes;
	
	public RouteFinderActor(ApplicationContext ctx) {
		super(ctx);
	}

	@Override
	protected void onReceive(FuseRequestMessage message) {
		
		String uri = message.getIncomingRequest()
				            .getUri();
		
		Optional<Route> route = routes.getFuseRoute(uri);
		
		if (route.isPresent()) {
			
			// add route to the message
			
			
			route.get()
			     .getHandler()
			     .tell(message, getSelf());
		}
		else {
			info("no handler for: " + uri);
		}
	}
	
}
