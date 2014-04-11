package com.sulaco.fuse;

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
		
		int a = 3;
	}

}
