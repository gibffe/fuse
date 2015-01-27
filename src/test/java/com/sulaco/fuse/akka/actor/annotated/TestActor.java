package com.sulaco.fuse.akka.actor.annotated;

import com.sulaco.fuse.akka.actor.FuseBaseActor;
import com.sulaco.fuse.config.annotation.FuseActor;
import org.springframework.context.ApplicationContext;

@FuseActor(id = "testActor", spin = 5)
public class TestActor extends FuseBaseActor {

}
