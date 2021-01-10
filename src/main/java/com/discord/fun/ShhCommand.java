package com.discord.fun;

import com.discord.BaseCommand;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ShhCommand extends BaseCommand {


    @Override
    public boolean execute(GuildMessageReceivedEvent event, String[] args) {

        textChannel.sendMessage("https://i.imgur.com/wA73szR.jpg").submit();

        return true;
    }
}
