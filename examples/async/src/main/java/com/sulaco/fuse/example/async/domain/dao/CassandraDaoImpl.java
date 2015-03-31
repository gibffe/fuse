package com.sulaco.fuse.example.async.domain.dao;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.sulaco.fuse.example.async.domain.entity.Playlist;
import com.sulaco.fuse.example.async.domain.entity.Song;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Component
public class CassandraDaoImpl implements CassandraDao {

    String node = "localhost";
    Cluster cluster;
    Session session;
    Executor executor = Executors.newFixedThreadPool(5);

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
    public void getSongById(String id, Function<Optional<?>, ?> consumer) {

        ResultSetFuture future
            = session.executeAsync(
                stmt_get_song.bind(UUID.fromString(id))
            );

        consumeAsync(future, consumer, rs -> songFrom(rs));
    }

    @Override
    public void getPlaylistById(String id, Function<Optional<?>, ?> consumer) {

        ResultSetFuture future
            = session.executeAsync(
                stmt_get_playlist.bind(id)
            );

        consumeAsync(future, consumer, rs -> playlistFrom(id, rs));
    }

    Optional<Playlist> playlistFrom(String id, ResultSet rs) {

        Playlist playlist = new Playlist(UUID.fromString(id));
        for (Row row : rs) {
            playlist.addSong(songFrom(row));
        }

        return Optional.of(playlist);
    }

    Optional<Song> songFrom(ResultSet rs) {
        Song song = null;
        if (rs.iterator().hasNext()) {
            song = songFrom(rs.iterator().next());
        }

        return Optional.ofNullable(song);
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

    void consumeAsync(ResultSetFuture future, Function<Optional<?>, ?> consumer, Function<ResultSet, Optional<?>> extractor) {
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
