package nl.underkoen.underbot.minesweeper.commands;

import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.commands.MainCommand;
import nl.underkoen.underbot.minesweeper.TileEmote;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class MinesweeperCommand implements MainCommand {
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
    public List<Command> getSubcommands() {
        return Arrays.asList(subcommands);
    }

    @Override
    public void setup() throws Exception {
        TileEmote.createFile();
    }
}
