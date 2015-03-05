package com.sulaco.fuse.codec;

import java.nio.ByteBuffer;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import st
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableMap;
import com.sulaco.fuse.FuseVersion;
import com.sulaco.fuse.akka.message.FuseRequestMessage;

@RunWith(MockitoJUnitRunner.class)
public class FuseWireProtocolTest {

	FuseWireProtocol classUnderTest;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS) FuseRequestMessage mockMessage;
	@Mock WireCodec   mockCodec;
	@Mock FuseVersion mockVersion;
	
	ByteBuffer data;
	
	Map<String, String> headers;
	
	@Before
	public void setup() {
		classUnderTest = new FuseWireProtocol();
		
		classUnderTest.codec   = mockCodec;
		classUnderTest.version = mockVersion;
		
		data = ByteBuffer.allocate(1);
		
		headers = ImmutableMap.of(
			"key1", "value1", 
			"key2", "value2"
		);
	}
	
	@Test
	public void testRespondRawFlushed() {
		
		// given
		when(mockMessage.flushed()).thenReturn(true);
		
		// when
		classUnderTest.respondRaw(mockMessage, OK, data, headers);
		
		// then
		verify(mockMessage, times(0)).flush();
	}
	
	@Test
	public void testRespondRaw() {
		
		// given
		when(mockMessage.flushed()).thenReturn(false);
		when(mockMessage.getRequest().headers().get(Connection)
		when(
			mockMessage.getChannelContext()
			           .channel()
			           .write(anyObject())
		)
		.thenAnswer(
				new Answer<Void>() {

					@Override
					public Void answer(InvocationOnMock invocation) throws Throwable {
						return null;
					}
				}
		);
		
		// when
		classUnderTest.respondRaw(message, status, data, headers);
	}
	
	@Test
	public void testRespondRawKeepAlive() { 
	
	}

}
