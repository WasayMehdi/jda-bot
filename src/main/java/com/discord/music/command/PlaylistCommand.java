package com.discord.music.command;

import com.discord.BaseCommand;
import com.discord.Bot;
import com.discord.Inject;
import com.discord.music.MusicPlayer;
import com.discord.music.Playlist;
import com.discord.music.PlaylistManager;
import com.discord.music.Song;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PlaylistCommand extends BaseCommand {

    @Override
    public boolean execute(GuildMessageReceivedEvent event, String[] args) {

        if(args.length < 2)
            return false;

        final String action = args[1];

        String playlistName = null;

        if(args.length >= 3)
            playlistName = args[2];

        switch (action) {
            case "create":

                if(ctx.getPlaylistManager().create(playlistName, member.getId())) {
                    textChannel.sendMessage("Created playlist " + playlistName).queue();
                } else {
                    textChannel.sendMessage("Unable to create duplicate playlist").queue();
                }

                break;

            case "add":

                final Song song = MusicPlayerCommand.forArgs(args, 3);

                if(song == null) {
                    textChannel.sendMessage("Unable to find song").submit();
                } else if(ctx.getPlaylistManager().add(playlistName, song)) {

                    textChannel.sendMessage("Successfully added " + song.getName() + " to " + playlistName).queue();

                } else {
                    textChannel.sendMessage("Unable to add " + song.getName() + " to playlist " + playlistName).queue();
                }

                break;

            case "shuffle":

                ctx.getPlaylistManager().forName(playlistName).ifPresent(playlist -> {
                    Collections.shuffle(playlist.getSongs());
                    listPlaylist(playlist);
                });

                break;

            case "remove":

                final Song removed = ctx.getPlaylistManager().remove(playlistName, Integer.parseInt(args[3]));

                if(removed != null) {
                    textChannel.sendMessage("Removed " + removed.getName() + " from list.").queue();
                }

                break;

            case "list":

                if(playlistName != null) {

                    ctx.getPlaylistManager().forName(playlistName).ifPresent(this::listPlaylist);

                } else {
                    int counter = 1;
                    for(Playlist playlist : ctx.getPlaylistManager().getPlaylists()) {
                        textChannel.sendMessage(counter + ". " + playlist.name + " Songs: " + playlist.getSongs().size())
                                .queue();
                    }

                }


                break;

            case "clear":

                ctx.getPlaylistManager().forName(playlistName).ifPresent(playlist -> {
                    playlist.getSongs().clear();
                    textChannel.sendMessage("Clearing all songs from " + playlist.name).submit();
                });

                break;
        }

        ctx.getPlaylistManager().save();

        return true;
    }

    private void listPlaylist(final Playlist playlist) {
        final AtomicInteger integer = new AtomicInteger();
        final StringBuilder listQueue = new StringBuilder();

        listQueue.append("Songs for '").append(playlist.name).append("'\n");

        playlist.getSongs().forEach(song -> {
            listQueue.append(integer.incrementAndGet())
                    .append(". ")
                    .append(song.getName())
                    .append("\n");
        });

        textChannel.sendMessage(listQueue.toString()).queue();
    }

    @Override
    public String help() {
        return "- playlist create [playlist name]\n" +
                "- add [playlist name] [song name/song link]\n" +
                "- shuffle [playlist name]\n" +
                "- list [optional: playlist name]\n" +
                "- clear [playlist name]\n" +
                "*To queue up a playlist do ~addlist [playlist name]*";
    }
}
