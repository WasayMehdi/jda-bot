package com.discord.music;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PlaylistManager {

    private static final String DELIMITER = " ### ";

    private final Map<String, Playlist> playlists;

    public PlaylistManager() {
        this.playlists = new HashMap<>();
    }

    public Optional<Playlist> forName(final String name) {
        return Optional.ofNullable(playlists.getOrDefault(name, null));
    }

    public boolean create(final String name, final String ownerId) {
        if (playlists.containsKey(name)) {
            return false;
        }

        playlists.put(name, new Playlist(name, ownerId));

        return true;
    }

    public boolean delete(final String name, final String ownerId) {
        if(!playlists.containsKey(name)) {
            return false;
        }

        final Playlist playlist = playlists.get(name);
        if(!ownerId.equals(playlist.ownerId)) {
            return false;
        }
        playlists.remove(name);
        return true;
    }

    public Song remove(final String playlistName, final int index) {

        return forName(playlistName)
                .map(playlist -> playlist.remove(index)).orElse(null);

    }

    public boolean add(final String playlistName, final Song song) {
        return forName(playlistName).map(playlist -> playlist.add(song)).orElse(false);
    }

    public Collection<Playlist> getPlaylists() {

        return playlists.values();

    }


    public void load() {

        try (final BufferedReader reader = new BufferedReader(new FileReader(getFile()))) {

            for (String s; (s = reader.readLine()) != null; ) {

                final String[] titles = s.split(DELIMITER);

                final String title = titles[0];
                final String ownerId = titles[1];
                final int lines = Integer.parseInt(titles[2]);

                final List<Song> songs = new LinkedList<>();

                for (int i = 0; i < lines; i++) {
                    s = reader.readLine();
                    final String[] songInfo = s.split(DELIMITER);

                    final Song song = new Song(songInfo[0], songInfo[1]);

                    songs.add(song);
                }

                final Playlist playlist = new Playlist(title, ownerId, songs);

                playlists.put(title, playlist);


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void save() {

        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(getFile(), false))) {

            for (Map.Entry<String, Playlist> entry : playlists.entrySet()) {

                final Playlist playlist = entry.getValue();

                writer.write(String.format("%s%s%s%s%s",
                        playlist.name, DELIMITER,
                        playlist.ownerId, DELIMITER,
                        playlist.getSongs().size()));

                writer.newLine();

                for (Song song : playlist.getSongs()) {

                    writer.write(song.getName());
                    writer.write(DELIMITER);
                    writer.write(song.getLink());
                    writer.newLine();

                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static File getFile() {
        final File file = new File("playlists.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
