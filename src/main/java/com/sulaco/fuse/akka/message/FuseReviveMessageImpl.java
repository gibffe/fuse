package com.sulaco.fuse.akka.message;

import java.util.UUID;

public class FuseReviveMessageImpl extends FuseInternalMessageImpl implements FuseReviveMessage {

    UUID id;
    Object payload;

    public FuseReviveMessageImpl(UUID id, Object payload) {
        this.id = id;
        this.payload = payload;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Object getPayload() {
        return payload;
    }
}
