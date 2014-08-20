package com.sulaco.fuse.akka.message;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unchecked")
public class FuseMessageContextImpl implements FuseMessageContext {

	private Optional<FuseRequestMessage> request;
	
	private ConcurrentMap<String, Object> context = new ConcurrentHashMap<>();
	
	@Override
	public int inc(String key) {
		context.computeIfAbsent(
					key, 
					e -> {
						return new AtomicInteger(0);
					}
		);

		return ((AtomicInteger) context.get(key)).incrementAndGet();
	}
	
	@Override
	public int dec(String key) {
		context.computeIfAbsent(
					key, 
					e -> {
						return new AtomicInteger(0);
					}
		);
		
		return ((AtomicInteger) context.get(key)).decrementAndGet();
	}
	
	@Override
	public <T> Optional<T> get(String key) {
		return (Optional<T>) Optional.ofNullable(
					context.getOrDefault(key, null)
		);
	}

	@Override
	public FuseMessageContext put(String key, Object value) {
		context.put(key, value);
		return this;
	}
	
	@Override
	public FuseMessageContext put(String key, Optional<Object> value) {
		value.ifPresent(
			val -> {
				put(key, val);
			}
		);
		return this;
	}

	@Override
	public Set<String> keys() {
		return context.keySet();
	}

	@Override
	public void setRequest(FuseRequestMessage request) {
		this.request = Optional.ofNullable(request);
	}

	@Override
	public Optional<FuseRequestMessage> getRequest() {
		return this.request;
	}
	
	

}
