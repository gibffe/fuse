package com.sulaco.fuse.config.route;

import java.util.Map;
import java.util.Optional;

public interface Route {

    public RouteHandler getHandler();
    
    public Map<String, String> getParams();
    
    public Optional<String> getParam(String name);
    
}
