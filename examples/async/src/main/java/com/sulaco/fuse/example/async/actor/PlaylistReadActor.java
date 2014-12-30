package com.sulaco.fuse.example.async.actor;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.akka.message.FuseRequestMessage;
import com.sulaco.fuse.example.async.domain.dao.CassandraDao;
import com.sulaco.fuse.example.async.domain.entity.Playlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class PlaylistReadActor extends FuseEndpointActor {

    @Autowired CassandraDao dao;

    public PlaylistReadActor(ApplicationContext ctx) {
        super(ctx);
    }

    @Override
    protected void onRequest(FuseRequestMessage request) {

        Optional<String> id = request.getParam("playlistId");

        if (id.isPresent()) {

            suspend(request);

            dao.getPlaylistById(
                id.get(),
                result -> revive(request.getId(), result)
            );
        }
        else {
            proto.respond(request, Playlist.EMPTY);
        }
    }
}

