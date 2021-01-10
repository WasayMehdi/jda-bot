package com.discord.fun;

import com.discord.BaseCommand;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.annotation.Nonnull;
import java.io.File;

public class RiftCommand  extends BaseCommand {


    @Override
    public boolean execute(GuildMessageReceivedEvent event, String[] args) {

        String imageLink = "C:\\Users\\smehd\\OneDrive\\VT\\Photos\\rift.jpg";

        if(args.length == 2) {
            switch (args[1].toLowerCase()) {
                case "phone":
                    imageLink = "C:\\Users\\smehd\\OneDrive\\VT\\Photos\\phonecall.png";
                    break;
            }

        }

        final Message report = new MessageBuilder()
                .append(event.getMember().getNickname())
                .append(" is signaling for all ")
                .append(new IMentionable() {
                    @Nonnull
                    @Override
                    public String getAsMention() {
                        return "<@&434418801031905295>";
                    }

                    @Override
                    public long getIdLong() {
                        return 434418801031905295L;
                    }
                })
                .build();

        textChannel.sendMessage(report)
                .addFile(new File(imageLink))
                .queue(m -> RiftCommand.this.message.delete().submit());

        return true;
    }


}
