package com.sulaco.fuse.example.async.actor;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.akka.message.FuseInternalMessage;
import com.sulaco.fuse.akka.message.FuseRequestMessage;
import com.sulaco.fuse.akka.message.FuseSuspendMessage;
import com.sulaco.fuse.example.async.domain.dao.CassandraDao;
import com.sulaco.fuse.example.async.domain.entity.Playlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class SongReadActor extends FuseEndpointActor {

    @Autowired CassandraDao dao;

    public SongReadActor(ApplicationContext ctx) {
        super(ctx);
    }

    @Override
    protected void onRequest(FuseRequestMessage request) {

        Optional<String> id = request.getParam("songId");

        if (id.isPresent()) {

            suspend(request);

            dao.getSongById(
                id.get(),
                result -> revive(request.getId(), result)
            );
        }
        else {
            proto.respond(request, Playlist.EMPTY);
        }
    }

    @Override
    public void onMessage(FuseInternalMessage message) {
        if (message instanceof FuseSuspendMessage) {
            Optional<Object> payload = message.getContext().get("payload");
            payload.ifPresent(
                data -> {
                    proto.respond(
                        message.getContext().getRequest().get(),
                        data
                    );
                }
            );
        }
    }
}
