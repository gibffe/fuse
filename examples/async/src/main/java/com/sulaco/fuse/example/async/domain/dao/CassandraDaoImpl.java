package com.sulaco.fuse.example.async.domain.dao;

import com.datastax.driver.core.*;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.sulaco.fuse.example.async.domain.entity.Playlist;
import com.sulaco.fuse.example.async.domain.entity.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
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

        consumeRowAsync(future, this::songFromRow, consumer);
    }

    @Override
    public void getPlaylistById(String id, Function<Optional<?>, ?> consumer) {

        ResultSetFuture future
            = session.executeAsync(
                stmt_get_playlist.bind(UUID.fromString(id))
            );

        consumeRowsAsync(future, rs -> playlistFromRows(id, rs), consumer);
    }

    Optional<Playlist> playlistFromRows(String id, Optional<List<Row>> rows) {

        Playlist playlist = new Playlist(UUID.fromString(id));
        rows.ifPresent(
            list -> list.forEach(
                        row -> playlist.addSong(songFromRow(row))
                    )
        );

        return Optional.of(playlist);
    }

    Optional<Song> songFromRow(Optional<Row> row) {
        return row.map(this::songFromRow);
    }

    Song songFromRow(Row row) {
        Song song = new Song();
        song.setId(row.getUUID("sid"));
        song.setArtist(row.getString("artist"));
        song.setAlbum(row.getString("album"));
        song.setTitle(row.getString("title"));
        song.setLength(row.getInt("length"));

        return song;
    }

   void consumeRowAsync (ResultSetFuture future, Function<Optional<Row>, Optional<?>> extractor, Function<Optional<?>, ?> consumer) {
        Futures.addCallback(
                future,
                new FutureCallback<ResultSet>() {
                    @Override
                    public void onSuccess (ResultSet rows) {
                        consumer.compose(extractor)
                                .compose(
                                    set -> {
                                        Iterator<Row> it = ((ResultSet) set).iterator();
                                        if (it.hasNext()) {
                                            return Optional.of(it.next());
                                        }
                                        return Optional.empty();
                                    }
                                )
                                .apply(rows);
                    }

                    @Override
                    public void onFailure (Throwable ex) {
                        log.error("Error executing async query !", ex);
                        consumer.compose(extractor)
                                .apply(Optional.empty());
                    }
                },
                executor
        );
    }

    void consumeRowsAsync (ResultSetFuture future, Function<Optional<List<Row>>, Optional<?>> extractor, Function<Optional<?>, ?> consumer) {
        Futures.addCallback(
                future,
                new FutureCallback<ResultSet>() {
                    @Override
                    public void onSuccess (ResultSet rows) {
                        consumer.compose(extractor)
                                .compose(
                                    set -> Optional.of(((ResultSet) set).all())
                                )
                                .apply(rows);
                    }

                    @Override
                    public void onFailure (Throwable ex) {
                        log.error("Error executing async query !", ex);
                        consumer.compose(extractor)
                                .apply(Optional.empty());
                    }
                },
                executor
        );
    }

    static final Logger log = LoggerFactory.getLogger(CassandraDaoImpl.class);

}
