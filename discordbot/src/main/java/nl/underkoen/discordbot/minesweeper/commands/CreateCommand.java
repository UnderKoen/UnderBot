package nl.underkoen.discordbot.minesweeper.commands;

import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.minesweeper.Minesweeper;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class CreateCommand implements DCommand {
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
    public void trigger(DContext context) {
        Minesweeper ms = new Minesweeper(context.getMember());
        ms.sendMap(context.getChannel());
    }
}
