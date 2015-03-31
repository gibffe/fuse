package com.sulaco.fuse.example.async.domain.dao;


import java.util.Optional;
import java.util.function.Function;

public interface CassandraDao {

    public void getSongById(String id, Function<Optional<?>, ?> fuseConsumer);

    public void getPlaylistById(String id, Function<Optional<?>, ?> fuseConsumer);
}
