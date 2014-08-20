package com.sulaco.fuse.codec;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;

import io.netty.handler.codec.http.HttpResponseStatus;

import com.sulaco.fuse.akka.message.FuseRequestMessage;

public interface WireProtocol {
	
	public void ok(FuseRequestMessage message);

	public void respond(FuseRequestMessage message, Object object);
	
	public void respond(FuseRequestMessage message, String content);
	
	public void respondRaw(FuseRequestMessage message, HttpResponseStatus status, ByteBuffer data, Map<String, String> headers);
	
	
	public void error(FuseRequestMessage message);
	
	public void error(FuseRequestMessage message, Object object);

	public void error(FuseRequestMessage message, String content);
	
	public void error(FuseRequestMessage message, HttpResponseStatus status, Object object);

	public void error(FuseRequestMessage message, HttpResponseStatus status, String content);
	
	
	public <T> Optional<T> read(FuseRequestMessage message, Class<T> clazz);
}
