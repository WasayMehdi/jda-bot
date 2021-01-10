package com.discord.fun;

import com.discord.BaseCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Turdify extends BaseCommand {


    @Override
    public boolean execute(GuildMessageReceivedEvent event, String[] args) {


        textChannel.sendMessage(turdify(String.join(" ", args).substring("turdify ".length())))
                .queue();;

        return false;
    }

    private String turdify(String arg) {

        if(arg.equalsIgnoreCase("faker")) {
            return "There is no way to turdify this";
        }

        final String[] newArgs = new String[] {
                arg.replaceFirst("[tT][aeiou]+[^aeiou]", "turd"),
                arg.replaceFirst("[tT]r[aeiou]+[^aeiou]", "turd"),
                arg.replaceFirst("t{2}\\w*", "turd"),
                arg.replaceFirst("2", "turd"),
                arg.replaceFirst("[Tt](?!h)", "$0urd")
        };

        for(final String str : newArgs) {
            if(!arg.equals(str)) {
                arg = str;
                break;
            }
        }

        return arg.replaceAll("[Ll]ebron", "turd");

    }
}
