package nl.underkoen.underbot.commands;

import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.entities.impl.CommandContextImpl;
import nl.underkoen.underbot.utils.Messages.ErrorMessage;
import nl.underkoen.underbot.utils.Messages.HelpMessage;
import nl.underkoen.underbot.utils.RoleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Under_Koen on 10-05-17.
 */
public interface MainCommand extends Command {
    List<Command> getSubcommands();

    @Override
    default void setup() throws Exception {
        List<Command> commands = getSubcommands();
        for (Command command : commands) {
            command.setup();
        }
    }

    @Override
    default void run(CommandContext context) throws Exception {
        List<Command> subcommands = getSubcommands();
        if (context.getArgs().length == 0) {
            new HelpMessage().addCommand(this).showSubcommands(true).sendMessage(context.getChannel());
        } else {
            HashMap<String, Command> commands = new HashMap<>();
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
            ;

            if (!commands.containsKey(commandName) && aliases.containsKey(commandName))
                commandName = aliases.get(commandName);

            Command command = commands.get(commandName);

            if (RoleUtil.getHighestRole(context.getMember()).getPosition() < command.getMinimumRole()) {
                new ErrorMessage(context.getMember(), "The minimum role for /" + command.getCommand() + " is " +
                        RoleUtil.getRole(context.getGuild(), command.getMinimumRole()).getName()).sendMessage(context.getChannel());
                context.getMessage().delete();
                return;
            }
            List<String> newArgs = Arrays.asList(context.getArgs());
            newArgs = new ArrayList<>(newArgs);
            newArgs.remove(0);

            List<String> newRawArgs = Arrays.asList(context.getRawArgs());
            newRawArgs = new ArrayList<>(newRawArgs);
            newRawArgs.remove(0);
            command.run(new CommandContextImpl()
                    .setArgs(newArgs.toArray(new String[0]))
                    .setRawArgs(newRawArgs.toArray(new String[0]))
                    .setChannel(context.getChannel())
                    .setCommand(command.getCommand())
                    .setGuild(context.getGuild())
                    .setMember(context.getMember())
                    .setUser(context.getUser())
                    .setPrefix(context.getPrefix())
                    .setMessage(context.getMessage())
            );
        }
    }
}
