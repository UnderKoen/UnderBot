package nl.underkoen.discordbot.minesweeper.commands;

import nl.underkoen.discordbot.commands.Command;
import nl.underkoen.discordbot.entities.CommandContext;
import nl.underkoen.discordbot.minesweeper.Map;
import nl.underkoen.discordbot.minesweeper.Minesweeper;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class FlagCommand implements Command {
    private String command = "flag";
    private String usage = "/flag [X location] [Y location]";
    private String description = "Flag [location] on the map.";

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
        if (context.getArgs().length < 2) {
            new ErrorMessage(context.getMember(), "This command needs arguments to work").sendMessage(context.getChannel());
            return;
        }
        Minesweeper ms = Minesweeper.getGame(context.getMember());
        Map map = ms.getMap();
        int x = Integer.parseInt(context.getArgs()[0]) - 1;
        int y = (int) context.getArgs()[1].toUpperCase().toCharArray()[0] - 65;
        map.flagTile(map.getLocationFromVisibleMap(x, y));
        ms.sendMap(context.getChannel());
    }
}