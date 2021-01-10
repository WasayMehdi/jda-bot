package com.discord.music;

import java.util.Objects;

public class Song {

    private final String link;
    private final String name;

    public Song(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(link, song.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link);
    }
}
