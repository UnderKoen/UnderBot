package nl.underkoen.underbot.minesweeper.commands;

import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.minesweeper.Minesweeper;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class ShowCommand implements Command {
    private String command = "show";
    private String usage = "/show";
    private String description = "Shows your map.";

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
        Minesweeper ms = Minesweeper.getGame(context.getMember());
        ms.sendMap(context.getChannel());
    }
}