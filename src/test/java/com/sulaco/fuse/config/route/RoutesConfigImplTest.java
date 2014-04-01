package com.sulaco.fuse.config.route;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import akka.actor.ActorSystem;

import com.sulaco.fuse.config.ConfigSource;
import com.sulaco.fuse.config.actor.ActorFactory;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

@RunWith(MockitoJUnitRunner.class)
public class RoutesConfigImplTest {

	Config config;
	
	@Mock
	ConfigSource mockConfigSource;
	
	@Mock
	ActorFactory mockActorFactory;
	
	@Mock
	ActorSystem mockSystem;
	
	private RoutesConfigImpl instance;
	
	@Before
	public void setup() {
		
		instance = new RoutesConfigImpl();

		config = ConfigFactory.load("test-1.conf");

		when(mockConfigSource.getConfig()).thenReturn(config);
		instance.configSource = mockConfigSource;
		instance.factory = mockActorFactory;
	}
	
	@Test
	public void testParseRoutes() {
		// given
		
		// when
		instance.parse();
		
		// then
		// TODO
	}
	
}
