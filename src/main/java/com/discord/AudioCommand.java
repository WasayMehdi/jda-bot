package com.discord;

import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public abstract class AudioCommand extends BaseCommand {

    protected BotAudioPlayer botAudioPlayer;

    public AudioCommand() {
        botAudioPlayer = new BotAudioPlayer();
    }

}
