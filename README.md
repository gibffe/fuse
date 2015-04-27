fuse [![Build Status](https://travis-ci.org/gibffe/fuse.svg)](https://travis-ci.org/gibffe/fuse/)
====

The main purpouse of this project is to serve as a boostrap into the world of reactive programming for average Joe Java Developer (like myself). What we have here is a micro rest server that enables creating actor based backend servers. Based on akka and netty, it has a solid foundation.

### Hello World server
-------------------------

There are few steps necessary to boot your first reactive server

> Add fuse as dependency

```xml
<dependency>
    <groupId>com.sulaco</groupId>
    <artifactId>fuse</artifactId>
    <version>0.0.3-SNAPSHOT</version>
</dependency>
```

> Create [fuse.conf](https://github.com/gibffe/fuse/blob/master/examples/simple/src/main/resources/fuse.conf) in resources folder. This is where we configure our routes.

```
routes {
    GET /hello {
        class : fuse.test.endpoint.Hello
    }
}
actors {

}
```




