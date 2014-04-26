package com.sulaco.fuse;

import static com.sulaco.fuse.TestUtil.*;
import static org.assertj.core.api.Assertions.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SuppressWarnings("unused")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/test-context.xml"})
public class FuseServerImplTest {

	boolean started = false;
	
	@Autowired FuseServer server;
	
	
	
	@Before
	public void setup() throws Exception {
		if (!started) {
			server.startServer();
			started = true;
		}
	}
	
	@Test
	public void testEcho() throws Exception {
		
		HttpClient client = getHttpClient();
		
		// when
		HttpResponse response = client.execute(new HttpGet("http://localhost:8080/fuse/echo"));
		
		// then
		assertThat(response).isNotNull();
	}
	
	@Test
	public void testPostEcho() throws Exception {
		
		HttpClient client = getHttpClient();
		
		// when
		HttpResponse response = client.execute(new HttpPost("http://localhost:8080/fuse/echo"));
	}

}
