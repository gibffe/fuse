package com.sulaco.fuse.akka.message;

import java.util.UUID;

public class FuseReviveMessageImpl extends FuseInternalMessageImpl implements FuseReviveMessage {

    long id;
    Object payload;

    public FuseReviveMessageImpl(long id, Object payload) {
        this.id = id;
        this.payload = payload;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Object getPayload() {
        return payload;
    }
}
