package nl.underkoen.discordbot.commands.general;

import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 21-04-17.
 */
public class InfoCommand implements DCommand {
    private String command = "info";
    private String usage = "/info";
    private String description = "Returns the bot VERSION.";

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
        new TextMessage().setMention(context.getMember())
                .addText("The current VERSION of the bot is: " + DiscordBot.VERSION)
                .addText("Use /changelog for more detail")
                .sendMessage(context.getChannel());
    }
}
