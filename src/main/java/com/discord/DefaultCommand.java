package com.discord;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Set;

public class DefaultCommand extends BaseCommand {

    private final Set<String> commandList;

    public DefaultCommand(final Set<String> commandList) {
        this.commandList = commandList;
    }

    @Override
    public boolean execute(GuildMessageReceivedEvent event, String[] args) {
        textChannel.sendMessage("List of available commands: " + commandList).queue();
        return false;
    }
}
