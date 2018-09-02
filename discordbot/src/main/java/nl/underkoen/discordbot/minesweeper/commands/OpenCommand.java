package nl.underkoen.discordbot.minesweeper.commands;

import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.minesweeper.Map;
import nl.underkoen.discordbot.minesweeper.Minesweeper;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class OpenCommand implements DCommand {
    private String command = "open";
    private String usage = "/open [X location] [Y location]";
    private String description = "Open [location] in your game.";

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
        if (context.getArgs().length < 2) {
            new ErrorMessage(context.getMember(), "This command needs arguments to work").sendMessage(context.getChannel());
            return;
        }
        Minesweeper ms = Minesweeper.getGame(context.getMember());
        Map map = ms.getMap();
        int x = Integer.parseInt(context.getArgs()[0]) - 1;
        int y = (int) context.getArgs()[1].toUpperCase().toCharArray()[0] - 65;
        map.openTile(map.getLocationFromVisibleMap(x, y));
        ms.sendMap(context.getChannel());
    }
}
