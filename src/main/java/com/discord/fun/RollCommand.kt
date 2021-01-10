package com.discord.`fun`

import com.discord.BaseCommand
import com.discord.Utility
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class RollCommand : BaseCommand() {
    override fun execute(event: GuildMessageReceivedEvent, args: Array<String>): Boolean {

        val min = Integer.parseInt(args[1])
        val max = Integer.parseInt(args[2])

        textChannel.sendMessage(member.nickname + " rolled a " + Utility.random(min, max))
                .queue()


        return false
    }

}
