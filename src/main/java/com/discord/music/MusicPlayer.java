package com.discord.music;

import com.discord.BotAudioPlayer;

import javax.swing.text.html.Option;
import java.util.*;

public class MusicPlayer {

    private final LinkedList<Song> songs;

    private Song current;
    private boolean looping;

    public MusicPlayer() {
        this.songs = new LinkedList<>();
    }

    public Optional<Song> next() {
        final Song next = songs.poll();
        current = next;

        if(next == null)
            return Optional.empty();

        if(looping)
            queue(current);

        return Optional.of(next);
    }

    public void queue(final Song song) {
        this.songs.add(song);
    }

    public void queue(final Playlist playlist) {
        songs.addAll(playlist.getSongs());
    }

    public void shuffle() {
        Collections.shuffle(songs);
    }

    public Song remove(int id) {
        if(id >= songs.size()) {
            return null;
        }

        return this.songs.remove(id);
    }

    public void push(final Song song) {
        songs.push(song);
    }

    public void stop() {
        this.songs.remove(current);
    }

    public void switchLoop() {
        looping = !looping;
    }

    public boolean isActive() {
        return current != null;
    }

    public Song getCurrent() {
        return current;
    }

    public List<Song> getQueue() {
        return songs;
    }

}
