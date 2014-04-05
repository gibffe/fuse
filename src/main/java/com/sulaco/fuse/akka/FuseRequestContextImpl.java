package com.sulaco.fuse.akka;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unchecked")
public class FuseRequestContextImpl implements FuseRequestContext {

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
	public void put(String key, Object value) {
		context.put(key, value);
	}

	@Override
	public Set<String> keys() {
		return context.keySet();
	}

}
