package com.discord;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public class BotAudioPlayer {

    boolean listening = false;

    private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    private static final AudioPlayer audioPlayer = playerManager.createPlayer();

    static {
        playerManager.registerSourceManager(new YoutubeAudioSourceManager());
        playerManager.registerSourceManager(new LocalAudioSourceManager());
    }


    BotAudioPlayer() {

    }

    public boolean load(final String identifier, final long position) {
        final Future<?> loaded = playerManager.loadItem(identifier, new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        audioPlayer.playTrack(track);
                        track.setPosition(position);
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        System.out.println("hello3");

                    }
                    @Override
                    public void noMatches() {
                        System.out.println("hello2");
                    }

                    @Override
                    public void loadFailed(FriendlyException throwable) {
                        System.out.println("hello");
                    }
                }
        );

        return false;
    }

    public boolean play(final VoiceChannel channel, final AudioManager manager, final OnTrackEnd callback) {

        if(!channel.equals(manager.getConnectedChannel())) {

            manager.openAudioConnection(channel);

        }

        audioPlayer.addListener(new AudioEventAdapter() {
            @Override
            public void onTrackStart(AudioPlayer player, AudioTrack track) {
                super.onTrackStart(player, track);

                // MySendHandler should be your AudioSendHandler implementation
                manager.setSendingHandler(new AudioSendHandler() {

                    AudioFrame lastFrame;

                    @Override
                    public boolean canProvide() {
                        lastFrame = audioPlayer.provide();
                        //System.out.println(lastFrame);
                        return lastFrame != null;
                    }

                    @Override
                    public ByteBuffer provide20MsAudio() {
                        return ByteBuffer.wrap(lastFrame.getData());
                    }

                    @Override
                    public boolean isOpus() {
                        return true;
                    }
                });
                // Here we finally connect to the target voice channel
                // and it will automatically start pulling the audio from the MySendHandler instance

            }

            @Override
            public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
                super.onTrackEnd(player, track, endReason);

                audioPlayer.removeListener(this);

                callback.execute(player, track, endReason);

            }
        });


        return true;
    }

    public boolean play(final GuildMessageReceivedEvent event) {
        return this.play(event, (a, b, c) -> {});
    }

    public boolean play(final GuildMessageReceivedEvent event, final OnTrackEnd callback) {
        Guild guild = event.getGuild();
        // This will get the first voice channel with the name "music"
        // matching by voiceChannel.getName().equalsIgnoreCase("music")

        if(event.getMember() == null || event.getMember().getVoiceState() == null) {
            return false;
        }

        VoiceChannel channel = event.getMember().getVoiceState().getChannel();

        if(channel == null) return false;

        AudioManager manager = guild.getAudioManager();

        return this.play(channel, manager, callback);
    }

    public void stop() {
        audioPlayer.stopTrack();
    }

    public void setVolume(int volume) {
        audioPlayer.setVolume(volume);
    }

    public interface OnTrackEnd {
        void execute(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason);
    }
}
