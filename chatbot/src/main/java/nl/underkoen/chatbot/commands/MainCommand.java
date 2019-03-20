package nl.underkoen.chatbot.commands;

import nl.underkoen.chatbot.models.Command;
import nl.underkoen.chatbot.models.Context;
import nl.underkoen.chatbot.models.RankAccessible;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Under_Koen on 03/09/2018.
 */
public abstract class MainCommand<COM extends Command<CON>, CON extends Context<?, COM, ?, ?, ?, ?>> implements Command<CON> {
    public abstract List<COM> getCommands();

    @Override
    public void setup() {
        getCommands().forEach(COM::setup);
    }

    @Override
    public void teardown() {
        getCommands().forEach(COM::teardown);
    }

    @Override
    public void trigger(CON context) {
        String[] args = context.getArgs();
        if (args.length < 1) {
            noCommand(context);
            return;
        }
        Optional<COM> commandO = getCommands().stream()
                .filter(command -> checkCommand(command, args))
                .findFirst();
        if (commandO.isPresent()) {
            COM command = commandO.get();
            context = modifyContext(context, command);
            if (canRun(context) && checkRank(context)) command.trigger(context);
        } else {
            noCommand(context);
        }
    }

    public boolean checkCommand(COM command, String[] args) {
        String cmd = args[0];
        return cmd.equals(command.getCommand()) || Arrays.stream(command.getAliases()).anyMatch(cmd::equalsIgnoreCase);
    }

    protected boolean checkRank(CON context) {
        COM command = context.getCommand();
        if (!(command instanceof RankAccessible)) return true;
        RankAccessible rankAccessible = (RankAccessible) command;
        return rankAccessible.getMinimumRank() <= context.getMember().getHighestRank();
    }

    protected abstract boolean canRun(CON context);

    protected abstract void noCommand(CON context);

    protected abstract CON modifyContext(CON context, COM command);
}
