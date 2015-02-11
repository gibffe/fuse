package com.sulaco.fuse.config;

import com.sulaco.fuse.config.route.RoutesConfig;
import com.typesafe.config.Config;

public interface ConfigSource {

    public void parseLocalConfig();
    
    public Config getConfig();
    
    public RoutesConfig getRoutesConfig();
}
