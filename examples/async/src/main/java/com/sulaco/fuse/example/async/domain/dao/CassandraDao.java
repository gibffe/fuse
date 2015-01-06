package com.sulaco.fuse.example.async.domain.dao;


import java.util.function.Function;

public interface CassandraDao {

    public void getSongById(String id, Function<Object, ?> fuseConsumer);

    public void getPlaylistById(String id, Function<Object, ?> fuseConsumer);
}
