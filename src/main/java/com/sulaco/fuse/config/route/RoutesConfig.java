package com.sulaco.fuse.config.route;

import java.util.Optional;

public interface RoutesConfig {

	public void parse();
	
	public Optional<Route> getFuseRoute(String uri);
}
