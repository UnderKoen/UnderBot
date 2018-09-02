package nl.underkoen.discordbot.commands;

import nl.underkoen.chatbot.models.Command;
import nl.underkoen.chatbot.models.RankAccessible;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.HelpMessage;
import nl.underkoen.discordbot.utils.RoleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Under_Koen on 10-05-17.
 */
public abstract class MainCommand implements DCommand {
    public abstract List<Command<DContext>> getSubcommands();

    @Override
    public void setup() {
        List<Command<DContext>> commands = getSubcommands();
        for (Command command : commands) {
            command.setup();
        }
    }

    @Override
    public void teardown() {
        List<Command<DContext>> commands = getSubcommands();
        for (Command command : commands) {
            command.teardown();
        }
    }

    @Override
    public void trigger(DContext context) {
        List<Command<DContext>> subcommands = getSubcommands();
        if (context.getArgs().length == 0) {
            new HelpMessage().addCommand(this).showSubcommands(true).sendMessage(context.getChannel());
        } else {
            HashMap<String, Command<DContext>> commands = new HashMap<>();
            HashMap<String, String> aliases = new HashMap<>();
            subcommands.forEach(command -> {
                commands.put(command.getCommand(), command);
            });
            subcommands.forEach(command -> {
                for (String alias : command.getAliases()) {
                    if (commands.containsKey(alias) || aliases.containsKey(alias)) continue;
                    aliases.put(alias, command.getCommand());
                }
            });

            String commandName = context.getArgs()[0];

            if (!commands.containsKey(commandName) && !aliases.containsKey(commandName)) {
                new HelpMessage().addCommand(this).showSubcommands(true).sendMessage(context.getChannel());
                return;
            }

            if (!commands.containsKey(commandName) && aliases.containsKey(commandName))
                commandName = aliases.get(commandName);

            Command<DContext> command = commands.get(commandName);

            if (command instanceof RankAccessible) {
                RankAccessible rankAccessible = (RankAccessible) command;
                if (RoleUtil.getHighestRole(context.getMember()).getPosition() < rankAccessible.getMinimumRank()) {
                    new ErrorMessage(context.getMember(), "The minimum role for /" + command.getCommand() + " is " +
                            RoleUtil.getRole(context.getServer(), rankAccessible.getMinimumRank()).getName()).sendMessage(context.getChannel());
                    return;
                }
            }
            List<String> newArgs = Arrays.asList(context.getArgs());
            newArgs = new ArrayList<>(newArgs);
            newArgs.remove(0);

            List<String> newRawArgs = Arrays.asList(context.getRawArgs());
            newRawArgs = new ArrayList<>(newRawArgs);
            newRawArgs.remove(0);

            context = new DContext(command, context.getMessage(), newArgs.toArray(new String[0]), newRawArgs.toArray(new String[0]));
            command.trigger(context);
        }
    }
}
