package com.sulaco.fuse.example.async.endpoint;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.akka.message.FuseInternalMessage;
import com.sulaco.fuse.akka.message.FuseRequestMessage;
import com.sulaco.fuse.example.async.domain.dao.CassandraDao;
import com.sulaco.fuse.example.async.domain.entity.Playlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class PlaylistReadActor extends FuseEndpointActor {

    @Autowired CassandraDao dao;

    @Override
    protected void onRequest(final FuseRequestMessage request) {

        Optional<String> id = request.getParam("playlistId");

        if (id.isPresent()) {

            // Once suspended request is revived, it will be passed towards an actor
            // represented by custom path. If suspended without a path, this actor is treated
            // as origin, and revival message will be sent back to it. Take a look at FuseBaseActor.onMessage
            //
            suspend(request);

            // we will cross thread boundaries inside the dao, take a look
            dao.getPlaylistById(
                id.get(),
                result -> revive(request, result)
            );
        }
        else {
            proto.respond(request, Playlist.EMPTY);
        }
    }

    // The revival message is delivered by suspended animation actor.
    //
    @Override
    protected void onRevive(FuseInternalMessage message, Optional<?> payload) {
        proto.respond(
            message.getContext()
                   .getRequest()
                   .get(),
            payload.get()
        );
    }
}

