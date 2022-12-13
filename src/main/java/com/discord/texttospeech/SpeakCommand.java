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
            String text = String.join(" ", args).substring(6);
            if (text.contains(";")) {
                text = text.substring(0, text.indexOf(";"));
            }
            TextToSpeechMp3 mp3 = new TextToSpeechMp3(text);

            final BotAudioPlayer.OnTrackEnd onTrackEnd = (a, b, c) -> mp3.destroy();

            final Optional<Member> mentioned = event.getMessage().getMentionedMembers().stream()
                    .findFirst();

            if(mentioned.isPresent()) {
                botAudioPlayer.play(mentioned.get().getVoiceState().getChannel(),
                        mentioned.get().getGuild().getAudioManager(),
                        onTrackEnd);
            } else {
                botAudioPlayer.play(voiceChannel, audioManager, onTrackEnd);
            }

            botAudioPlayer.load(mp3.getAudioFile().getAbsolutePath(), 0);

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }
}
