package com.sulaco.fuse.example.async.domain.dao;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.sulaco.fuse.example.async.domain.entity.Playlist;
import com.sulaco.fuse.example.async.domain.entity.Song;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Component
public class CassandraDaoImpl implements CassandraDao {

    String node = "localhost";
    Cluster cluster;
    Session session;
    Executor executor = Executors.newCachedThreadPool();

    PreparedStatement stmt_get_playlist;
    PreparedStatement stmt_get_song;

    void init() {
        cluster = Cluster.builder()
                         .addContactPoint(node)
                         .build();

        session = cluster.connect("test");

        stmt_get_playlist = session.prepare("select * from playlist where pid = ?");
        stmt_get_song     = session.prepare("select * from song     where sid = ?");
    }

    @Override
    public void getSongById(String id, Function consumer) {

        ResultSetFuture future
            = session.executeAsync(
                stmt_get_song.bind(UUID.fromString(id))
            );

        consumeAsync(
            future,
            rs -> songFrom(rs),
            consumer
        );
    }

    @Override
    public void getPlaylistById(String id, Function<Object, ?> consumer) {

        ResultSetFuture future
            = session.executeAsync(
                stmt_get_playlist.bind(id)
            );

        consumeAsync(
            future,
            rs -> playlistFrom(id, rs),
            consumer
        );
    }

    Playlist playlistFrom(String id, ResultSet rs) {

        Playlist playlist = new Playlist(UUID.fromString(id));
        for (Row row : rs) {
            playlist.addSong(songFrom(row));
        }

        return playlist;
    }

    Song songFrom(ResultSet rs) {
        Song song = Song.EMPTY;
        if (rs.iterator().hasNext()) {
            song = songFrom(rs.iterator().next());
        }
        return song;
    }

    Song songFrom(Row row) {
        Song song = new Song();
        song.setId(row.getUUID("sid"));
        song.setArtist(row.getString("artist"));
        song.setAlbum(row.getString("album"));
        song.setTitle(row.getString("title"));
        song.setLength(row.getInt("length"));

        return song;
    }

    void consumeAsync(ResultSetFuture future, Function<ResultSet, ?> extractor, Function<Object, ?> consumer) {
        Futures.addCallback(
            future,
            new FutureCallback<ResultSet>() {
                @Override public void onSuccess(ResultSet rows) {
                    consumer.compose(extractor)
                            .apply(rows);
                }

                @Override public void onFailure(Throwable throwable) {
                    consumer.compose(extractor)
                            .apply(null);
                }
            },
            executor
        );
    }


}
