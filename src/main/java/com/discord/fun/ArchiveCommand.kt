package com.discord.`fun`

import com.discord.BaseCommand
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class ArchiveCommand : BaseCommand() {
    override fun execute(event: GuildMessageReceivedEvent, args: Array<out String>): Boolean {
        val builder = StringBuilder("```\n");
        textChannel.iterableHistory.takeAsync(args.getOrNull(1)?.toInt() ?: 100)
                .thenApply { it.filter { it.author == event.author } }
                .thenApply { it.map { builder.append(archive(it)).append("\n"); it } }
                .whenComplete { _, _ ->
                    guild.textChannels.first { "archive" == it.name }
                            .sendMessage(builder.append("\n```").toString()).queue()
                }
                .thenApply { it.forEach { it.delete().queue(); } }

        return true;
    }

    private fun archive(message: Message): String {
        return "[${message.timeCreated.toLocalDateTime()}] ${message.author.name}:  ${message.contentDisplay}";
    }

    override fun help() : String {
        return "~archive [optional: number of message to traverse, default 100]"
    }

}