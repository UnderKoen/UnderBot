package nl.underkoen.chatbot.commands;

import nl.underkoen.chatbot.models.Command;
import nl.underkoen.chatbot.models.Context;

import java.util.function.Supplier;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public class SimpleCommand<CON extends Context> implements Command<CON> {
    private String prefix;
    private String command;
    private String description;
    private Supplier<String> response;
    private Messager<CON> messager;

    public SimpleCommand(String prefix, String command, String description, Supplier<String> response) {
        SimpleCommand(prefix, command, description, response, );
    }

    public SimpleCommand(String prefix, String command, String description, Supplier<String> response, Messager<CON> messager) {
        this.prefix = prefix;
        this.command = command;
        this.description = description;
        this.response = response;
        this.messager = messager;
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
    public void trigger(CON context) {
        context.getChannel().sendMessage(response.get());
    }

    public interface Messager<CON extends Context> {

    }
}
