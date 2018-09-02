package nl.underkoen.discordbot.commands.general;

import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;

/**
 * Created by Under_Koen on 19-04-17.
 */
public class TestCommand implements DCommand {
    private String command = "test";
    private String usage = "/test";
    private String description = "This is a test command.";

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
    }
}