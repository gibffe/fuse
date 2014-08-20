package com.sulaco.fuse.akka.message;

import java.util.Optional;
import java.util.Set;

public interface FuseMessageContext {

	public int inc(String key);
	
	public int dec(String key);
	
	public <T> Optional<T> get(String key);
	
	public FuseMessageContext put(String key, Object value);
	
	public FuseMessageContext put(String key, Optional<Object> value);
	
	public Set<String> keys();
	
	
	public void setRequest(FuseRequestMessage request);
	
	public Optional<FuseRequestMessage> getRequest();
	
}
