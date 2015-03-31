package com.sulaco.fuse.akka.message;

import java.util.Optional;

public class FuseReviveMessageImpl extends FuseInternalMessageImpl implements FuseReviveMessage {

    long id;
    Optional<?> payload;

    public FuseReviveMessageImpl(long id, Optional<?> payload) {
        this.id = id;
        this.payload = payload;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public Optional<?> getPayload() {
        return payload;
    }
}
