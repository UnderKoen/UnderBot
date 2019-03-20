package nl.underkoen.discordbot.commands.general;

import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.utils.Messages.HelpMessage;

/**
 * Created by Under_Koen on 21-04-17.
 */
public class HelpCommand implements DCommand {
    private String command = "help";
    private String usage = "/help";
    private String description = "Returns all commands.";

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
        new HelpMessage()
                .setMention(context.getMember())
                .showSubcommands(true)
                .addCommands(DiscordBot.handler.getCommands())
                .sendMessage(context.getChannel());
    }
}
