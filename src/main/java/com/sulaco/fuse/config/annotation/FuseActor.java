package com.sulaco.fuse.config.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FuseActor {

    String id() default "";

    int spin() default 1;

}
