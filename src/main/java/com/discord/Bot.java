package com.discord;

import com.discord.music.PlaylistManager;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class Bot {

    private PlaylistManager playlistManager;


    private final JDABuilder jdaBuilder;
    private final BotAudioPlayer audioPlayer;

    public Bot() {

        this.jdaBuilder = JDABuilder.createDefault("NDM2NjA2NjI5MzkyMjg1Njk3.WtjrnA.P6NOTCLRONFVPOr5AoR_26zpeJ0");
        this.audioPlayer = new BotAudioPlayer();


    }

    public void init() throws LoginException {

        jdaBuilder.addEventListeners(new CommandHandler(this));

        jdaBuilder.build();

        playlistManager = new PlaylistManager();

        playlistManager.load();

    }

    public PlaylistManager getPlaylistManager() {
        return playlistManager;
    }

}
