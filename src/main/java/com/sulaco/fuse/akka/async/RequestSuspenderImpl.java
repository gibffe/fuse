package com.sulaco.fuse.akka.async;


import akka.actor.ActorSelection;
import com.sulaco.fuse.akka.message.FuseInternalMessage;
import com.sulaco.fuse.config.ConfigSource;
import com.sulaco.fuse.config.actor.ActorFactory;
import com.typesafe.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class RequestSuspenderImpl implements RequestSuspender {

    @Autowired ConfigSource configSource;

    @Autowired ActorFactory actorFactory;

    long sweepInterval;
    long sweepTimeout;

    // Cryo is meant for holding incoming requests in suspended animation, until some unit of work,
    // executed asynchronously, is completed and ready to deliver payload. Upon revival, the payload
    // (if any) will be handed over to appropriate actor (origin or bounce target) and further processing
    // resumes.
    //
    // Cryo will not hold onto these requests forever - there is an active sweeper scanning & terminating
    // requests as necessary. Current version is using a global timeout value (unfortunate).
    //
    NavigableMap<Long, FuseInternalMessage> cryo;


    public RequestSuspenderImpl() {
        initCryo();
        initSweeper();
    }

    void initCryo() {
        cryo = new ConcurrentSkipListMap<>();
    }

    void initSweeper() {
        Config cfg = configSource.getConfig();

        this.sweepInterval = cfg.getLong("fuse.animator.sweep.interval");
        this.sweepTimeout  = cfg.getLong("fuse.animator.sweep.timeout");

        Executors.newScheduledThreadPool(1)
                 .scheduleAtFixedRate(
                         () -> sweepCryo(),
                         0,
                         sweepInterval,
                         TimeUnit.MILLISECONDS
                 );
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

    void sweepCryo() {
        // since we can navigate this map in request id order and since ids are ever increasing (until flip to 0)
        // we only ever scan the tail until first request that does not need to be collected
        //

        Optional<ActorSelection> selection = actorFactory.select("/fuse/user/channelReaper");

        selection.ifPresent(
            reaper -> {

                long timestamp;
                long now = System.currentTimeMillis();

                for (Map.Entry<Long, FuseInternalMessage> entry : cryo.entrySet()) {
                    try {

                        timestamp = entry.getValue().getTimestamp();

                        if (now - timestamp >= sweepTimeout) {
                            reaper.tell(entry.getValue());
                        }
                    }
                    catch(IllegalStateException ise) {
                        // Entry no longer in cryo - keep going.
                    }
                }

            }
        );

    }

}
