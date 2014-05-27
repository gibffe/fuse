package com.sulaco.fuse.codec;

import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaders.Names.SERVER;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sulaco.fuse.FuseVersion;
import com.sulaco.fuse.akka.message.FuseRequestMessage;

@Component
public class FuseWireProtocol implements WireProtocol {

	@Autowired WireCodec codec;
	
	@Autowired FuseVersion version;
	
	Map<String, String> defaultHeaders;
	
	public FuseWireProtocol() {
		super();
		defaultHeaders = new HashMap<>();
		defaultHeaders.put(SERVER       , version.toString());
		defaultHeaders.put(CONTENT_TYPE , APP_JSON);
	}
	
	@Override
	public void ok(FuseRequestMessage message) {
		respond(message, OK);
	}

	@Override
	public void respond(FuseRequestMessage message, Object object) {
		respond(message, codec.getString(object));
	}

	@Override
	public void respond(FuseRequestMessage message, String content) {
		respond(message, HttpResponseStatus.OK, content);
	}
	
	@Override
	public void error(FuseRequestMessage message) {
		error(message, HttpResponseStatus.BAD_REQUEST, BAD_REQUEST);
	}

	@Override
	public void error(FuseRequestMessage message, Object object) {
		error(message, codec.getString(object));		
	}

	@Override
	public void error(FuseRequestMessage message, String content) {
		error(message, HttpResponseStatus.BAD_REQUEST, content);
		
	}

	@Override
	public void error(FuseRequestMessage message, HttpResponseStatus status, Object object) {
		error(message, status, codec.getString(object));
	}

	@Override
	public void error(FuseRequestMessage message, HttpResponseStatus status, String content) {
		respond(message, status, content);
	}

	@Override
	public <T> Optional<T> read(FuseRequestMessage request, Class<T> clazz) {
		return Optional.ofNullable(
				codec.getObject(request.getRequestBody(), clazz)
		);
	}

	protected void respond(FuseRequestMessage message, HttpResponseStatus status, String content) {
		respondRaw(message, status, ByteBuffer.wrap(content.getBytes()), defaultHeaders);
	}
	
	@Override
	public void respondRaw(FuseRequestMessage message, HttpResponseStatus status, ByteBuffer data, Map<String, String> headers) {
		if (!message.flushed()) {
			boolean keepAlive = isKeepAlive(message.getRequest());
	        
			FullHttpResponse response 
				= new DefaultFullHttpResponse(
									HTTP_1_1, 
									status, 
									Unpooled.wrappedBuffer(data)
				  );
			
	        response.headers().set(CONTENT_LENGTH , response.content().readableBytes());
	        
	        for (Map.Entry<String, String> entry : headers.entrySet()) {
	        	response.headers().set(entry.getKey(), entry.getValue());
	        }
	
	        if (!keepAlive) {
	            message.getChannelContext()
	            	   .write(response)
	                   .addListener(ChannelFutureListener.CLOSE);
	        } 
	        else {
	            response.headers()
	                    .set(
	                    		CONNECTION, 
	                    		Values.KEEP_ALIVE
	                    );
	            
	            message.getChannelContext()
	                   .channel()
	                   .write(response);
	        }
	        
	        message.flush();
		}
	}



	public static final String OK          = "";
	public static final String BAD_REQUEST = "{x_x}";
	public static final String APP_JSON    = "application/json";

}
