package com.sulaco.fuse.codec;

public interface WireCodec {

	<T> T getObject(String data, Class<T> clazz);
	
	<T> T getObject(byte[] data, Class<T> clazz);
	
	String getString(Object object);
	
	byte[] getBytes(Object object);
}
