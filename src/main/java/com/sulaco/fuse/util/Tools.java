package com.sulaco.fuse.util;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sulaco.fuse.akka.message.FuseRequestMessage;

public class Tools {

	private static ConcurrentMap<String, ConcurrentMap<String, String>> keyCache
		= new ConcurrentHashMap<>();
	
	private static ConcurrentMap<String, Optional<Method>> methodCache 
		= new ConcurrentHashMap<>();
	
	public static <T> Optional<T> empty() {
		return Optional.empty();
	}
	
	public static final <T> Optional<T> optional(T value) {
		return Optional.ofNullable(value);
	}
	
	public static final Optional<Method> lookupMethod(final Object target, final String methodName) {
		
		Class<?> clazz = target.getClass();
		
		String key = getMethodKey(clazz.getName(), methodName);
		
		methodCache
			.computeIfAbsent(
				key, 
				k -> {
					Method method = null;
					
					try {
						method = clazz.getMethod(methodName, FuseRequestMessage.class);
					}
					catch (Exception ex) {
						log.warn("{} does not contain a fuse compatible {} method !", clazz.getName(), methodName, ex);
					}
					//
					return optional(method);
				}
		    );
		
		return methodCache.get(key);
	}
	
	private static final String getMethodKey(final String clazz, final String methodName) {

		keyCache.computeIfAbsent(clazz, 
								 k -> {
									 return new ConcurrentHashMap<String, String>();
								 }
		);
		
		keyCache.get(clazz)
				.computeIfAbsent(methodName, 
								 k -> {
									 return new StringBuilder().append(clazz)
											 				   .append(methodName)
											 				   .toString()
									 ;
								 }
				);
		//
		return keyCache.get(clazz).get(methodName);
	}

	private static final Logger log = LoggerFactory.getLogger(Tools.class);
}
