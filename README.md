fuse [![Build Status](https://travis-ci.org/gibffe/fuse.svg)](https://travis-ci.org/gibffe/fuse/)
====

The main purpouse of this project is to serve as a boostrap into the world of reactive programming for busy Java Developers (like myself). Fuse is a work in progress micro rest server that enables creating actor based backends. Fuse has been built on top of [Netty](https://github.com/netty/netty) & [Akka](https://github.com/akka/akka).

### Hard dependencies
-------------------------
Java 8, Spring

### Hello World server
-------------------------

There are few steps necessary to boot your first reactive server

> Clone the repo & install fuse locally, then add as dependency.

```xml
<dependency>
    <groupId>com.sulaco</groupId>
    <artifactId>fuse</artifactId>
    <version>0.0.3-SNAPSHOT</version>
</dependency>
```

> Create [fuse.conf](https://github.com/gibffe/fuse/blob/master/examples/simple/src/main/resources/fuse.conf) in resources folder of your new project. This is where we configure our routes.

```
routes {
    GET /hello {
        class : fuse.test.endpoint.HelloWorld
    }
}
actors {

}
```

> Create spring application [context.xml](https://github.com/gibffe/fuse/blob/master/examples/simple/src/main/resources/context.xml) file. Fuse currently requires spring to operate.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	   xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	   xmlns:context="http://www.springframework.org/schema/context"
	   xsi:schemaLocation="http://www.springframework.org/schema/beans
						   http://www.springframework.org/schema/beans/spring-beans-4.0.xsd
						   http://www.springframework.org/schema/context
						   http://www.springframework.org/schema/context/spring-context-4.0.xsd"
       default-init-method="init"
>

    <context:annotation-config />
    <context:component-scan base-package="com.sulaco.fuse" />
    <bean class="com.sulaco.fuse.codec.JsonWireCodec" />

</beans>
```

> Create HelloWorld endpoint actor

```java
package fuse.test.endpoint.HelloWorld;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.akka.message.FuseRequestMessage;

public class HelloWorld extends FuseEndpointActor {
    
    @Override
    protected void onRequest(FuseRequestMessage request) {
        proto.respond(request, "Hello World !\n");
    }

}

```

> Bootstrap the server - create [Bootstrap.java](https://github.com/gibffe/fuse/blob/master/examples/simple/src/main/java/com/sulaco/fuse/Bootstrap.java) file which will serve as an application entry point.

```java
package fuse.test;

import com.sulaco.fuse.FuseBootstrap;

public class Bootstrap extends FuseBootstrap {

    public static void main(String[] args) throws Exception {
        FuseBootstrap.main(args);
    }

}
```

Running the Bootstrap will start the server up.

### Examples

There are few examples available [here](https://github.com/gibffe/fuse/tree/master/examples)


