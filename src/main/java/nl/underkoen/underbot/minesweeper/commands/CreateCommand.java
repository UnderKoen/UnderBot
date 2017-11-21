package nl.underkoen.underbot.minesweeper.commands;

import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.minesweeper.Minesweeper;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class CreateCommand implements Command {
    private String command = "create";
    private String usage = "/create";
    private String description = "Create a game of minesweeper.";

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
    public void setup() throws Exception {

    }

    @Override
    public void run(CommandContext context) throws Exception {
        Minesweeper ms = new Minesweeper(context.getMember());
        ms.sendMap(context.getChannel());
    }
}
