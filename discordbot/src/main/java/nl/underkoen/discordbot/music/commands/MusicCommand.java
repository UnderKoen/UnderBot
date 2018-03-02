package nl.underkoen.discordbot.music.commands;

import nl.underkoen.discordbot.Main;
import nl.underkoen.discordbot.commands.Command;
import nl.underkoen.discordbot.commands.MainCommand;
import nl.underkoen.discordbot.music.MusicHandler;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class MusicCommand implements MainCommand {
    private String command = "music";
    private String[] aliases = {"m"};
    private String usage = "/music [subcommand]";
    private String description = "This is the main music command.";
    private Command[] subcommands = {new PlayCommand(), new PlaylistCommand(), new SearchPlayCommand(), new QueueCommand(), new NextCommand(), new ForceNextCommand(),
            new JoinCommand(), new LeaveCommand(), new DefaultCommand(), new VolumeCommand()};

    public static MusicHandler musicHandler;

    public MusicCommand() {
        musicHandler = new MusicHandler();
        Main.client.getDispatcher().registerListener(musicHandler);
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String[] getAliases() {
        return aliases;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<Command> getSubcommands() {
        return Arrays.asList(subcommands);
    }
}