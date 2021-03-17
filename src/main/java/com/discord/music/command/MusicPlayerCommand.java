package com.discord.music.command;

import com.discord.AudioCommand;
import com.discord.music.MusicPlayer;
import com.discord.music.Playlist;
import com.discord.music.PlaylistManager;
import com.discord.music.Song;
import com.google.common.collect.Lists;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeSearchProvider;
import com.sedmelluq.discord.lavaplayer.track.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class MusicPlayerCommand extends AudioCommand {

    public static final List<String> COMMANDS = Arrays.asList("play", "skip", "restart", "queue", "shuffle", "remove", "addlist", "volume", "clear");

    private static final YoutubeSearchProvider youtubeSearchProvider
            = new YoutubeSearchProvider();

    private static final YoutubeAudioSourceManager youtubeAudioSourceManager
            = new YoutubeAudioSourceManager();

    private final MusicPlayer musicPlayer = new MusicPlayer();

    private final Stack<CompletableFuture<Message>> nextPlayingMessages = new Stack<>();

    public MusicPlayerCommand() {
        super();
        youtubeAudioSourceManager.setPlaylistPageCount(10);
    }

    @Override
    public boolean execute(GuildMessageReceivedEvent event, String[] args) {

        switch (args[0]) {
            case "play":
                //play(args[1]);
                if(args.length < 2) {
                    return false;
                }

                final Song song = forArgs(args, 1);

                if(song == null) {
                    textChannel.sendMessage("No results").queue();
                    return false;
                }

                textChannel.sendMessage("Now queueing up: " + song.getName()).queue();

                play(song);


                break;

            case "skip":
                if(musicPlayer.getCurrent() == null) {
                    System.out.println("There is no song playing");
                    return false;
                }
                textChannel.sendMessage("Skipping: " + musicPlayer.getCurrent().getName()).submit();
                botAudioPlayer.stop();
                break;
            case "restart":
                musicPlayer.push(musicPlayer.getCurrent());
                botAudioPlayer.stop();

                break;

            case "queue":

                listQueue();

                break;

            case "shuffle":

                musicPlayer.shuffle();

                listQueue();

                break;

            case "remove":

                int index = Integer.parseInt(args[1]);

                if(index < 1) {
                    textChannel.sendMessage("Use ~skip to remove current song").queue();
                    return false;
                }

                final Song removed = musicPlayer.remove(index-1);

                if(removed != null) {
                    textChannel.sendMessage("Removing " + removed.getName() ).submit();
                }

                break;

            case "addlist":
                final Optional<Playlist> playlist
                        = ctx.getPlaylistManager().forName(args[1]);

                if(playlist.isPresent()) {
                    playlist.get().getSongs().forEach(this::play);
                    textChannel.sendMessage("Queued up playlist " + playlist.get().name).queue();
                } else {

                    textChannel.sendMessage("Unable to queue up " + args[2]).queue();
                }
                break;

            case "clear":

                musicPlayer.getQueue().clear();

                textChannel.sendMessage("Cleared the song queue").queue();

                break;

            case "volume":

                botAudioPlayer.setVolume(Integer.parseInt(args[1]));

                break;

        }


        return true;
    }

    private void play(final Song song) {

        musicPlayer.queue(song);

        if(!musicPlayer.isActive()) {
            playNextSong();
        }

    }

    private void playNextSong() {
        musicPlayer.next().ifPresent(song -> {
            System.out.println("Loading: " + song + " with link: " + song.getLink());
            if(!nextPlayingMessages.empty()) {
                nextPlayingMessages.pop().thenApply(message -> message.delete().submit());
            }
            botAudioPlayer.play(voiceChannel, audioManager, (a, b, endReason) -> {
                playNextSong();
            });
            botAudioPlayer.load(song.getLink(), 0);
            nextPlayingMessages.push(
                    textChannel.sendMessage("Now playing: " + song.getName()).submit());
        });

    }

    private void listQueue() {

        final AtomicInteger integer = new AtomicInteger();
        final StringBuilder listQueue = new StringBuilder();

        Lists.partition(musicPlayer.getQueue(), 30)
                .stream()
                .map(list -> PlaylistCommand.forSongList(list, integer))
                .map(textChannel::sendMessage).forEach(MessageAction::queue);

    }

    public static Song forArgs(final String[] args, final int index) {
        if(args[index].startsWith("http://") || args[index].startsWith("https://")) {
            return new Song(args[index], args[index]);
        }

        final StringBuilder builder = new StringBuilder();

        for(int i = index ; i < args.length; i++) {
            builder.append(args[i]).append(" ");
        }

        final String concat = builder.toString().trim();

        final AudioItem item = youtubeSearchProvider.loadSearchResult(concat, (audioTrackInfo -> new YoutubeAudioTrack(audioTrackInfo, youtubeAudioSourceManager)));

        if(item == AudioReference.NO_TRACK) {
            return null;
        } else {
            final AudioPlaylist audioPlaylist = ((BasicAudioPlaylist) item);

            final AudioTrack track = audioPlaylist.getTracks().get(0);

            return new Song(track.getInfo().title, track.getInfo().uri);
        }
    }

}
