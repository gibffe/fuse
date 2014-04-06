package com.sulaco.fuse.akka;

import java.util.Optional;
import java.util.Set;

public interface FuseRequestContext {

	public int inc(String key);
	
	public int dec(String key);
	
	public <T> Optional<T> get(String key);
	
	public void put(String key, Object value);
	
	public Set<String> keys();
}
