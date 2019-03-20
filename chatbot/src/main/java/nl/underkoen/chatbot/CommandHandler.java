package nl.underkoen.chatbot;

import com.sun.istack.internal.NotNull;
import nl.underkoen.chatbot.models.Command;
import nl.underkoen.chatbot.models.Context;
import nl.underkoen.chatbot.models.Message;
import nl.underkoen.chatbot.models.RankAccessible;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public abstract class CommandHandler<COM extends Command<CON>,
        CON extends Context<?, COM, ?, MES, ?, ?>, MES extends Message> {
    protected List<COM> commands = new ArrayList<>();

    public void registerCommand(@NotNull COM command) {
        if (command == null) throw new IllegalArgumentException("Command should not be null");
        if (commands.stream().anyMatch(cmd -> cmd.getPrefix().equals(command.getPrefix()) &&
                cmd.getCommand().equalsIgnoreCase(command.getCommand())))
            throw new IllegalArgumentException("Command with same prefix and command already exist in the system.");
        commands.add(command);
    }

    public void removeCommand(COM command) {
        commands.remove(command);
    }

    public List<COM> getCommands() {
        return commands;
    }

    public void setup() {
        commands.forEach(COM::setup);
    }

    public void teardown() {
        commands.forEach(COM::teardown);
    }

    //TODO check aliases last
    public void check(MES message) {
        commands.stream()
                .filter(command -> checkCommand(command, message))
                .findFirst()
                .ifPresent(command -> {
                    CON context = toContext(command, message);
                    if (canRun(context) && checkRank(context)) command.trigger(context);
                });
    }

    protected boolean checkCommand(COM command, MES message) {
        String content = message.getContent();
        if (!content.startsWith(command.getPrefix())) return false;
        content = content.replaceFirst(command.getPrefix(), "");
        String cmd = content.split(" ", 2)[0];
        return cmd.equals(command.getCommand()) || Arrays.stream(command.getAliases()).anyMatch(cmd::equalsIgnoreCase);
    }

    protected boolean checkRank(CON context) {
        COM command = context.getCommand();
        if (!(command instanceof RankAccessible)) return true;
        RankAccessible rankAccessible = (RankAccessible) command;
        return rankAccessible.getMinimumRank() <= context.getMember().getHighestRank();
    }

    protected abstract boolean canRun(CON context);

    @NotNull
    protected abstract CON toContext(COM command, MES message);
}
