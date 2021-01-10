package com.discord;

import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public abstract class BaseCommand implements Command {

    protected TextChannel textChannel;
    protected Member member;
    protected Message message;
    protected Guild guild;
    protected VoiceChannel voiceChannel;
    protected AudioManager audioManager;
    protected Bot ctx;

    @Override
    public void initialize(GuildMessageReceivedEvent event, Bot ctx) {
        this.ctx = ctx;
        this.textChannel = event.getChannel();
        this.member = event.getMember();
        this.message = event.getMessage();
        this.guild = event.getGuild();

        if(event.getMember() != null && event.getMember().getVoiceState() != null) {
            this.voiceChannel = event.getMember().getVoiceState().getChannel();
        }

        if(voiceChannel != null)
            this.audioManager = guild.getAudioManager();
    }

    public abstract boolean execute(GuildMessageReceivedEvent event, String[] args);



}
