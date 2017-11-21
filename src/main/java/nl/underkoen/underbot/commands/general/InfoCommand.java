package nl.underkoen.underbot.commands.general;

import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 21-04-17.
 */
public class InfoCommand implements Command {
    private String command = "info";
    private String usage = "/info";
    private String description = "Returns the bot version.";

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
        new TextMessage().setMention(context.getMember())
                .addText("The current version of the bot is: " + Main.version)
                .addText("Use /changelog for more detail")
                .sendMessage(context.getChannel());
    }
}
