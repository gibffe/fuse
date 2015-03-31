package com.sulaco.fuse.example.async.endpoint;

import com.sulaco.fuse.akka.actor.FuseEndpointActor;
import com.sulaco.fuse.akka.message.FuseInternalMessage;
import com.sulaco.fuse.akka.message.FuseRequestMessage;
import com.sulaco.fuse.example.async.domain.dao.CassandraDao;
import com.sulaco.fuse.example.async.domain.entity.Playlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class SongReadActor extends FuseEndpointActor {

    @Autowired CassandraDao dao;

    @Override
    protected void onRequest(FuseRequestMessage request) {

        Optional<String> id = request.getParam("songId");

        if (id.isPresent()) {

            // Once suspended request is revived, it will be passed back to this actor and handled in
            // the onRevive method. If not overridden, by default, it will push received result on to the wire.
            // Take a look at FuseBaseActor.onMessage for details.
            suspend(request);

            // we will cross thread boundaries inside the dao, take a look
            dao.getSongById(
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
    protected void onRevive(FuseInternalMessage request, Optional<?> payload) {
        proto.respond(
            request.getContext()
                   .getRequest()
                   .get(),
            payload
        );
    }
}
