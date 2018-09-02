package nl.underkoen.chatbot;

import com.sun.istack.internal.NotNull;
import nl.underkoen.chatbot.models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public abstract class CommandHandler<CH extends Channel<S>, CON extends Context<CH, MEM, MES, S, U>,
        MEM extends Member<S, U>, MES extends Message<CH, MEM, S, U>, S extends Server<CH, MEM, U>, U extends User> {
    private List<Command<CON>> commands = new ArrayList<>();

    public void registerCommand(@NotNull Command<CON> command) {
        if (command == null) throw new IllegalArgumentException("Command should not be null");
        if (commands.stream().anyMatch(cmd -> cmd.getPrefix().equals(command.getPrefix()) &&
                cmd.getCommand().equalsIgnoreCase(command.getCommand())))
            throw new IllegalArgumentException("Command with same prefix and command already exist in the system.");
        commands.add(command);
    }

    public void removeCommand(Command<CON> command) {
        commands.remove(command);
    }

    public List<Command<CON>> getCommands() {
        return commands;
    }

    public void setup() {
        commands.forEach(Command::setup);
    }

    public void teardown() {
        commands.forEach(Command::teardown);
    }

    public void check(MES message) {
        Optional<Command<CON>> commandO = commands.stream()
                .filter(command -> checkCommand(command, message))
                .findFirst();
        commandO.ifPresent(command -> {
            CON context = toContext(command, message);
            if (canRun(context) && checkRank(context)) command.trigger(context);
        });
    }

    public boolean checkCommand(Command<CON> command, MES message) {
        String content = message.getContent();
        if (!content.startsWith(command.getPrefix())) return false;
        content = content.replaceFirst(command.getPrefix(), "");
        String cmd = content.split(" ", 2)[0];
        return cmd.equals(command.getCommand()) || Arrays.stream(command.getAliases()).anyMatch(cmd::equalsIgnoreCase);
    }

    public boolean checkRank(CON context) {
        Command command = context.getCommand();
        if (!(command instanceof RankAccessible)) return true;
        RankAccessible rankAccessible = (RankAccessible) command;
        return rankAccessible.getMinimumRank() <= context.getMember().getHighestRank();
    }

    public abstract boolean canRun(CON context);

    @NotNull
    public abstract CON toContext(Command<CON> command, MES message);
}
