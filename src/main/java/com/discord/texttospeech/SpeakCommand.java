package com.discord.texttospeech;

import com.discord.AudioCommand;
import com.discord.BotAudioPlayer;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;
import java.util.Optional;

public class SpeakCommand extends AudioCommand {

    @Override
    public boolean execute(GuildMessageReceivedEvent event, String[] args) {

        if (!"234068153112592386".equals(message.getAuthor().getId())) {
            return false;
        }

        message.delete().submit();

        try {
            TextToSpeechMp3 mp3 = new TextToSpeechMp3(String.join(" ", args).substring(6));
            botAudioPlayer.load(mp3.getAudioFile().getAbsolutePath(), 0);

            final BotAudioPlayer.OnTrackEnd onTrackEnd = (a, b, c) -> mp3.destroy();

            botAudioPlayer.play(event, onTrackEnd);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;

    }
}
