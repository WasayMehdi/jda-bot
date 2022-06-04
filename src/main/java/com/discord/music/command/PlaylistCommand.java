package com.discord.music.command;

import com.discord.BaseCommand;
import com.discord.Bot;
import com.discord.Inject;
import com.discord.music.MusicPlayer;
import com.discord.music.Playlist;
import com.discord.music.PlaylistManager;
import com.discord.music.Song;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

            case "import":
                if(playlistName == null) {
                    textChannel.sendMessage("Use this command as [~playlist import playlist_name]").queue();
                    return false;
                }
                final List<Message.Attachment> attachments = event.getMessage().getAttachments();
                System.out.println("ATTACHMENTS: " + attachments);
                if(attachments.size() != 1) {
                    textChannel.sendMessage("Please only attach one file to this command").queue();
                    return false;
                }
                final Message.Attachment attachment = attachments.get(0);
                if(!"csv".equals(attachment.getFileExtension())) {
                    textChannel.sendMessage("Please get a csv file from https://exportify.net/").queue();
                    return false;
                }

                textChannel.sendMessage("Loading playlist: " + attachment.getFileName()).submit();

                final ExecutorService executorService = Executors.newFixedThreadPool(10);
                final List<Song> songs = Collections.synchronizedList(new ArrayList<>());

                try(final BufferedReader reader = new BufferedReader(
                        new InputStreamReader(attachment.retrieveInputStream().join(), StandardCharsets.UTF_8))) {
                    reader.readLine(); // remove headers

                    for (String line; (line = reader.readLine()) != null;) {
                        final String[] parts = line.split(",");
                        final String title = parts[1];
                        final String author = parts[3];

                        final String[] searchTerm = (title + " - " + author).split(" ");

                        executorService.submit(() -> {
                            final Song song = MusicPlayerCommand.forArgs(searchTerm, 0);

                            if (song == null) {
                                textChannel.sendMessage("Could not find: " + title).queue();
                            } else {
                                songs.add(song);
                                textChannel.sendMessage("Added: " + song.getName()).queue();
                            }
                        });

                    }
                } catch (IOException e) {
                    textChannel.sendMessage("Failed to grab playlist: " + e.getMessage()).submit();
                }

                try {
                    executorService.shutdown();
                    executorService.awaitTermination(10, TimeUnit.MINUTES);
                    if(ctx.getPlaylistManager().create(playlistName, member.getId())) {
                        textChannel.sendMessage("Created playlist " + playlistName).queue();
                    }
                    for (Song s : songs) {
                        ctx.getPlaylistManager().add(playlistName, s);
                    }
                    textChannel.sendMessage("Loaded " + songs.size() + " songs into " + playlistName).queue();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
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

            case "delete":
                if(!ctx.getPlaylistManager().delete(playlistName, member.getId())) {
                    textChannel.sendMessage("Cannot delete playlist " + playlistName).submit();
                } else {
                    textChannel.sendMessage("Deleted playlist: " + playlistName).submit();
                }
                break;
        }

        ctx.getPlaylistManager().save();

        return true;
    }

    private void listPlaylist(final Playlist playlist) {
        final AtomicInteger integer = new AtomicInteger();

        textChannel.sendMessage("Songs for " + playlist.name).queue();
        Lists.partition(playlist.getSongs(), 30)
                .stream()
                .map(list -> forSongList(list, integer))
                .map(textChannel::sendMessage).forEach(MessageAction::queue);

    }

    public static String forSongList(final List<Song> songs, final AtomicInteger integer) {
        final StringBuilder listQueue = new StringBuilder("```");
        songs.forEach(song -> {
            listQueue.append(integer.getAndIncrement())
                    .append(". ")
                    .append(song.getName())
                    .append("\n");
        });
        listQueue.append("```");
        return listQueue.toString();
    }

    @Override
    public String help() {
        return "- playlist create [playlist name]\n" +
                "- add [playlist name] [song name/song link]\n" +
                "- shuffle [playlist name]\n" +
                "- list [optional: playlist name]\n" +
                "- clear [playlist name]\n" +
                "- import [playlist name] (attach csv from https://exportify.net/)\n" +
                "- delete [playlist name]\n" +
                "*To queue up a playlist do ~addlist [playlist name]*";
    }
}
