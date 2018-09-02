package nl.underkoen.chatbot;

import nl.underkoen.chatbot.models.Command;
import nl.underkoen.chatbot.models.Context;

import java.util.function.Supplier;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public class SimpleCommand implements Command {
    private String prefix;
    private String command;
    private String description;
    private Supplier<String> response;

    public SimpleCommand(String prefix, String command, String description, Supplier<String> response) {
        this.prefix = prefix;
        this.command = command;
        this.description = description;
        this.response = response;
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getUsage() {
        return getPrefix() + getCommand();
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void trigger(Context context) {
        context.getChannel().sendMessage(response.get());
    }
}
