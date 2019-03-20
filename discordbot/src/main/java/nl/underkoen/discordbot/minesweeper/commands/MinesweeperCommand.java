package nl.underkoen.discordbot.minesweeper.commands;

import nl.underkoen.chatbot.models.Command;
import nl.underkoen.discordbot.commands.DMainCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.minesweeper.TileEmote;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class MinesweeperCommand extends DMainCommand {
    private String command = "minesweeper";
    private String usage = "/minesweeper [subcommand]";
    private String description = "This is the main minesweeper command.";

    private Command[] subcommands = {new OpenCommand(), new FlagCommand(), new CreateCommand(), new ShowCommand(), new CheckCommand()};

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ms"};
    }

    @Override
    public List<Command<DContext>> getCommands() {
        return Arrays.asList(subcommands);
    }

    @Override
    public void setup() {
        TileEmote.createFile();
        super.setup();
    }
}
