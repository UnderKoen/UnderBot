package nl.underkoen.underbot.commands.general;

import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.utils.Messages.HelpMessage;

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
