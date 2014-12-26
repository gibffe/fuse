package com.sulaco.fuse.akka.async;


import com.sulaco.fuse.akka.message.FuseInternalMessage;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Component
public class RequestSuspenderImpl implements RequestSuspender {

    ConcurrentNavigableMap<Long, FuseInternalMessage> cryo;

    public RequestSuspenderImpl() {
        cryo = new ConcurrentSkipListMap<>();
        // TODO: add sweeper mechanics
        //
    }

    @Override
    public void suspend(FuseInternalMessage message) {

        message.timestamp()
               .getContext()
               .getRequest()
               .ifPresent(
                   r -> cryo.put(r.getId(), message)
               );
    }

    @Override
    public Optional<FuseInternalMessage> revive(long id) {
        return Optional.of(cryo.remove(id));
    }

}
