package com.sulaco.fuse.akka.message;

public interface FuseReviveMessage {

    long getId();

    Object getPayload();
}
