package com.sulaco.fuse.config.route;

import static org.assertj.core.api.Assertions.*;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import akka.actor.ActorRef;
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
		ActorRef mockActorRef = mock(ActorRef.class);
		
		when(mockActorFactory.getLocalActor(anyString(), anyString(), anyInt()))
			.thenReturn(Optional.ofNullable(mockActorRef));
		
		when(mockActorFactory.getLocalActorByRef(anyString()))
			.thenReturn(Optional.ofNullable(mockActorRef));
		
		// when
		instance.parse();
		
		// then
			
			// assert /simple/rest/path
			assertThat(
				instance.root.children.get("simple")
				             .children.get("rest")
				             .children.get("path")
		    ).isNotNull();
			
			assertThat(
				instance.root.children.get("simple").handler().isPresent()	
			).isFalse();
			
			assertThat(
				instance.root.children.get("simple")
				   			 .children.get("rest")
				   			 .handler.isPresent()
			).isFalse();
			
			assertThat(
				instance.root.children.get("simple")
				             .children.get("rest")
				             .children.get("path")
				             .handler.isPresent()
					
			).isTrue();
			
			// assert /simple/rest/<uid>
			assertThat(
				instance.root.children.get("simple")
							 .children.get("rest")
							 .children.get("*")
			).isNotNull();
			
			assertThat(
				instance.root.children.get("simple")
							 .children.get("rest")
							 .children.get("*")
							 .handler.isPresent()
			).isTrue();
			
			// assert /test/<uid>/delete
			assertThat(
					instance.root.children.get("test")
								 .children.get("*")
								 .children.get("delete")
			).isNotNull();

			assertThat(
					instance.root.children.get("test")
								 .children.get("*")
								 .children.get("delete")
								 .handler.isPresent()
			).isTrue();
			
			// assert /simple/actor
			assertThat(
					instance.root.children.get("simple")
								 .children.get("actor")
			).isNotNull();
			
			assertThat(
					instance.root.children.get("simple")
								 .children.get("actor")
								 .handler.isPresent()
			).isTrue();
	}
	
	@Test
	public void testGetFuseRoute() {
		
		// given
		instance.parse();
		
		// when
		Optional<Route> route = instance.getFuseRoute("http://localhost:8080/simple/rest/path");
		
		// then
		assertThat(route).isNotNull();
		assertThat(route.isPresent()).isTrue();
		assertThat(route.get().getParams()).isEmpty();
	}
	
	@Test
	public void testGetFuseDynamicRoute() {
		
		// given
		instance.parse();
		
		// when
		Optional<Route> route = instance.getFuseRoute("http://localhost:8080/simple/rest/12345");
		
		// then
		assertThat(route).isNotNull();
		assertThat(route.isPresent()).isTrue();
		
		Optional<String> uid = route.get().getParam("uid");
		assertThat(uid.isPresent());
		assertThat(uid.get()).isEqualTo("12345");
	}
	
}
