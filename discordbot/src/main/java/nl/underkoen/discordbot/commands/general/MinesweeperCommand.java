package nl.underkoen.discordbot.commands.general;

import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.minesweeper.Map;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 21/03/2019.
 */
public class MinesweeperCommand implements DCommand {
    @Override
    public String getCommand() {
        return "minesweeper";
    }

    @Override
    public String getUsage() {
        return "/minesweeper";
    }

    @Override
    public String getDescription() {
        return "Creates a minesweeper game to play.";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ms"};
    }

    @Override
    public void trigger(DContext context) {
        Map map = new Map();
        new TextMessage().setMention(context.getMember())
                .addText(map.toMessage())
                .sendMessage(context.getChannel());
    }
}
