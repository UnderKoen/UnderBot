package nl.underkoen.discordbot.commands;

import nl.underkoen.chatbot.commands.MainCommand;
import nl.underkoen.chatbot.models.Command;
import nl.underkoen.chatbot.models.RankAccessible;
import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.HelpMessage;
import nl.underkoen.discordbot.utils.RoleUtil;

import java.util.Arrays;

/**
 * Created by Under_Koen on 10-05-17.
 */
public abstract class DMainCommand extends MainCommand<Command<DContext>, DContext> {
    @Override
    public String getPrefix() {
        return DiscordBot.PREFIX;
    }

    @Override
    protected void noCommand(DContext context) {
        new HelpMessage().addCommand(this).showSubcommands(true).sendMessage(context.getChannel());
    }

    @Override
    protected DContext modifyContext(DContext context, Command<DContext> command) {
        String[] args = context.getArgs();
        args = Arrays.copyOfRange(args, 1, args.length);
        String[] rawArgs = context.getRawArgs();
        rawArgs = Arrays.copyOfRange(rawArgs, 1, rawArgs.length);
        return new DContext(command, context.getMessage(), args, rawArgs);
    }

    @Override
    protected boolean canRun(DContext context) {
        Command command = context.getCommand();
        if (!(command instanceof RankAccessible)) return true;
        RankAccessible rankAccessible = (RankAccessible) command;
        if (RoleUtil.getHighestRole(context.getMember()).getPosition() >= rankAccessible.getMinimumRank()) return true;
        new ErrorMessage(context.getMember(), "The minimum role for /" + command.getCommand() + " is " +
                RoleUtil.getRole(context.getServer(), rankAccessible.getMinimumRank()).getName()).sendMessage(context.getChannel());
        return false;
    }
}
