package com.sulaco.fuse.example.async.domain.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Playlist {

    UUID id;
    Set<Song> songs;

    public Playlist() {
        this.songs = new HashSet<>();
    }

    public Playlist(UUID id) {
        this();
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public Set<Song> getSongs() {
        return songs;
    }

    public void addSong(Song song) {
        this.songs.add(song);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Playlist)) return false;

        Playlist playlist = (Playlist) o;

        if (!id.equals(playlist.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public static final Playlist EMPTY = new Playlist();
}
