package com.discord.fun;

import com.discord.BaseCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.IOException;

public class RestartCommand extends BaseCommand {
    @Override
    public boolean execute(GuildMessageReceivedEvent event, String[] args) {
        ProcessBuilder pb = new ProcessBuilder("nohup restart.sh > /dev/null 2>/dev/null &");
        try {
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
