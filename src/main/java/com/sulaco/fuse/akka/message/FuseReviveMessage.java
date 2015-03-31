package com.sulaco.fuse.akka.message;

import java.util.Optional;

public interface FuseReviveMessage {

    long getId();

    Optional<?> getPayload();
}
