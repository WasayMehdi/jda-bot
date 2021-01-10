package com.discord;

import com.discord.fun.*;
import com.discord.meme.MemeCommand;
import com.discord.music.command.MusicPlayerCommand;
import com.discord.music.command.PlaylistCommand;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class CommandHandler extends ListenerAdapter {

    private static final String COMMAND_PREFIX = "~";

    private static final Map<String, Command> COMMAND_MAP = new HashMap<>();

    private Bot context;

    private static final DefaultCommand DEFAULT_COMMAND;

    static {
        COMMAND_MAP.put("meme", new MemeCommand());
        COMMAND_MAP.put("roll", new RollCommand());
        COMMAND_MAP.put("lata", new LataCommand());
        COMMAND_MAP.put("shh", new ShhCommand());
        COMMAND_MAP.put("rift", new RiftCommand());
        COMMAND_MAP.put("dcane", new DCaneCommand());
        COMMAND_MAP.put("ring", new RingCommand());
        COMMAND_MAP.put("playlist", new PlaylistCommand());
        COMMAND_MAP.put("turdify", new Turdify());
        COMMAND_MAP.put("dogify", new Dogify());
        COMMAND_MAP.put("archive", new ArchiveCommand());

        final MusicPlayerCommand musicPlayerCommand = new MusicPlayerCommand();

        for(String s : MusicPlayerCommand.COMMANDS) {
            COMMAND_MAP.put(s, musicPlayerCommand);
        }

        DEFAULT_COMMAND = new DefaultCommand(COMMAND_MAP.keySet());
    }

    CommandHandler(final Bot bot) {

        this.context = bot;

    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {

        if (event.getAuthor().isBot()) return;

        final String messageStr = event.getMessage().getContentStripped();

        if(!messageStr.startsWith(COMMAND_PREFIX)) return;

        final String[] args = messageStr.substring(1).split(" ");

        final String cmdStr = args[0];

        final Command command = COMMAND_MAP.getOrDefault(cmdStr, DEFAULT_COMMAND);

        if(args.length == 2 && "help".equals(args[1])) {
            final String helpMessage = new StringBuilder()
                    .append("Help for: ").append(COMMAND_PREFIX).append(cmdStr).append("\n")
                    .append("```\n")
                    .append(command.help())
                    .append("\n```")
                    .toString();
            event.getMessage().getTextChannel().sendMessage(helpMessage).queue();
            return;
        }

        System.out.printf("Executing command: %s: %s \n", cmdStr, Arrays.toString(args));

        command.initialize(event, context);

        command.execute(event, args);


    }

}
