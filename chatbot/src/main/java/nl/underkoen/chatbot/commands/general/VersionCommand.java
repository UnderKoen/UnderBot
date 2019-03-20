package nl.underkoen.chatbot.commands.general;

import nl.underkoen.chatbot.commands.SimpleCommand;

import java.util.function.Supplier;

/**
 * Created by Under_Koen on 03/09/2018.
 */
public class VersionCommand extends SimpleCommand {
    private static final String COMMAND = "version";
    private static final String DESCRIPTION = "returns the current version of the bot";

    public VersionCommand(String prefix, Supplier version) {
        super(prefix, COMMAND, DESCRIPTION, version);
    }
}
