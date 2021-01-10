package com.discord;


import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

interface Command {

    void initialize(GuildMessageReceivedEvent event, Bot ctx);

    boolean execute(GuildMessageReceivedEvent event, String[] args);

    default String help() {
        return "No help configured";
    }

}
