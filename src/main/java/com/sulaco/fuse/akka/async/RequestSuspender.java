package com.sulaco.fuse.akka.async;


import com.sulaco.fuse.akka.message.FuseInternalMessage;

import java.util.Optional;

public interface RequestSuspender {

    void suspend(FuseInternalMessage message);

    Optional<FuseInternalMessage> revive(long id);

}
