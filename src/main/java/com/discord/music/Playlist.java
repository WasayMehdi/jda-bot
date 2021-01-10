package com.discord.music;


import java.util.*;

public class Playlist {

    public final String ownerId;
    public final String name;
    private List<Song> songs;

    public Playlist(final String name, final String ownerId, final List<Song> songs) {
        this.name = name;
        this.songs = songs;
        this.ownerId = ownerId;
    }

    public Playlist(final String name, final String ownerId) {
        this(name, ownerId, new LinkedList<>());
    }

    public boolean add(final Song song) {

        if(songs.contains(song)) {
            return false;
        }

        return this.songs.add(song);
    }

    public void remove(final Song song) {
        this.songs.remove(song);
    }

    public Song remove(final int id) {
        if(id >= songs.size()) {
            return null;
        }
        return this.songs.remove(id);
    }

    public List<Song> getSongs() {
        return songs;
    }




}
