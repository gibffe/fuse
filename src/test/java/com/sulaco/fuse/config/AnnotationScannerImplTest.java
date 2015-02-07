package com.sulaco.fuse.config;

import akka.actor.ActorRef;
import com.sulaco.fuse.config.actor.ActorFactory;
import com.sulaco.fuse.config.route.RoutesConfig;
import com.typesafe.config.Config;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationScannerImplTest {

    @Mock Config mockConfig;
    @Mock ConfigSource mockConfigSource;
    @Mock RoutesConfig mockRoutesConfig;
    @Mock ActorFactory mockFactory;

    AnnotationScannerImpl classUnderTest;

    @Before
    public void setup() {

        classUnderTest = new AnnotationScannerImpl();

        when(mockConfigSource.getConfig()).thenReturn(mockConfig);
        when(mockFactory.getLocalActor(anyString(), anyString(), anyInt())).thenReturn(Optional.of(mock(ActorRef.class)));

        classUnderTest.config  = mockConfigSource;
        classUnderTest.factory = mockFactory;
        classUnderTest.routes  = mockRoutesConfig;
    }

    @Test
    public void testInitWithPackage() {

        // given
        when(mockConfig.getString("fuse.scan.packages")).thenReturn("com.sulaco.fuse.akka.actor.annotated");
        when(mockConfig.getBoolean("fuse.scan.verbose")).thenReturn(false);

        // when
        classUnderTest.init();

        // then
        assertThat(classUnderTest.reflections).isNotNull();
    }

    @Test
    public void testInitWithoutPackage() {
        // given
        when(mockConfig.getString("fuse.scan.packages")).thenReturn("");
        when(mockConfig.getBoolean("fuse.scan.verbose")).thenReturn(false);

        // when
        classUnderTest.init();

        // then
        assertThat(classUnderTest.reflections).isNull();
    }

    @Test
    public void testScan() {
        // given
        when(mockConfig.getString("fuse.scan.packages")).thenReturn("com.sulaco.fuse.akka.actor.annotated");
        when(mockConfig.getBoolean("fuse.scan.verbose")).thenReturn(false);
        classUnderTest.init();

        AnnotationScannerImpl instance = spy(classUnderTest);

        // when
        instance.scan();

        // then
        verify(instance, times(1)).processActor(any(Class.class));
        verify(instance, times(1)).processEndpoint(any(Class.class));
        verify(mockRoutesConfig, times(1)).addEndpoint(any(ActorRef.class), anyString(), anyString());
    }

}
