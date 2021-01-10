package com.discord.fun;

import com.discord.AudioCommand;
import com.discord.BotAudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class LataCommand extends AudioCommand {
    @Override
    public boolean execute(GuildMessageReceivedEvent event, String[] args) {

        message.delete().submit();

        botAudioPlayer.load("LATASHORT.mp3", 0);

        final Optional<Member> mentioned = event.getMessage().getMentionedMembers().stream()
                .findFirst();
        final BotAudioPlayer.OnTrackEnd onTrackEnd = (a, b, c) -> audioManager.closeAudioConnection();

        if(mentioned.isPresent()) {
            System.out.println("Mentioned: " + mentioned);
            botAudioPlayer.play(mentioned.get().getVoiceState().getChannel(),
                    mentioned.get().getGuild().getAudioManager(),
                    onTrackEnd);
        } else {
            botAudioPlayer.play(event, onTrackEnd);
        }



        return true;

    }
}
