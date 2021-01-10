package com.discord.fun;

import com.discord.BaseCommand;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Dogify extends BaseCommand {


    @Override
    public boolean execute(GuildMessageReceivedEvent event, String[] args) {


        textChannel.sendMessage(dogify(String.join(" ", args).substring("dogify ".length())))
                .queue();;

        return false;
    }

    private String dogify(String arg) {

        if(arg.equalsIgnoreCase("faker")) {
            return "There is no way to dogify this";
        }

        final String[] newArgs = new String[] {
                arg.replaceFirst("d{2}\\w*", "dog"),
                arg.replaceFirst("[dD][aeiou]+[^aeiou]", "dog"),
                arg.replaceFirst("[dD]r[aeiou]+[^aeiou]", "dog"),
        };

        for(final String str : newArgs) {
            if(!arg.equals(str)) {
                arg = str;
                break;
            }
        }

        return arg = arg.replaceAll("[Ll]ebron", "dog");

    }
}
