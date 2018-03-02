package nl.underkoen.discordbot.commands.general;

import nl.underkoen.discordbot.Main;
import nl.underkoen.discordbot.commands.Command;
import nl.underkoen.discordbot.entities.CommandContext;
import nl.underkoen.discordbot.utils.Messages.HelpMessage;

/**
 * Created by Under_Koen on 21-04-17.
 */
public class HelpCommand implements Command {
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
    public void setup() throws Exception {

    }

    @Override
    public void run(CommandContext context) {
        new HelpMessage()
                .setMention(context.getMember())
                //.showSubcommands(true)
                .addCommands(Main.handler.getAllCommands())
                .sendMessage(context.getChannel());
    }
}
