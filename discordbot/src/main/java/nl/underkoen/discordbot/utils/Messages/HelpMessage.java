package nl.underkoen.discordbot.utils.Messages;

import nl.underkoen.chatbot.models.Command;
import nl.underkoen.chatbot.models.RankAccessible;
import nl.underkoen.discordbot.commands.MainCommand;
import nl.underkoen.discordbot.entities.DChannel;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.utils.ColorUtil;
import nl.underkoen.discordbot.utils.RoleUtil;
import sx.blah.discord.handle.impl.obj.Embed;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 21-04-17.
 */
public class HelpMessage extends UnderMessage {
    private Color color = ColorUtil.hexToColor("#2a6886");

    private DMember user;

    private String message;

    private boolean subcommands;

    private List<Command<DContext>> commands;

    public HelpMessage setMention(DMember user) {
        this.user = user;
        return this;
    }

    public HelpMessage addText(String text) {
        if (message == null) {
            message = "";
        } else {
            message = message + "\n";
        }
        message = message + text;
        return this;
    }

    public HelpMessage addCommand(Command<DContext> command) {
        if (this.commands == null) this.commands = new ArrayList<>();
        this.commands.add(command);
        return this;
    }

    public HelpMessage addCommands(List<Command<DContext>> commands) {
        if (this.commands == null) this.commands = new ArrayList<>();
        this.commands.addAll(commands);
        return this;
    }

    public HelpMessage showSubcommands(boolean show) {
        subcommands = show;
        return this;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public DMember getAuthor() {
        return user;
    }

    @Override
    public String getDescription() {
        return message;
    }

    @Override
    public void lastCheck(EmbedBuilder msg, DChannel channel) {
        DMember author = getAuthor();
        int role = RoleUtil.getHighestRole(channel.getServer()).getPosition();
        if (author != null) {
            role = RoleUtil.getHighestRole(author).getPosition();
        }

        List<IRole> roles = new ArrayList<>(channel.getServer().getGuild().getRoles());
        int finalRole = role;
        List<Command> mainCommands = new ArrayList<>();
        roles.forEach(role1 -> {
            if (role1.getPosition() > finalRole) return;
            List<Command> roleCommands = new ArrayList<>(commands);
            roleCommands.removeIf(command -> {
                if (!(command instanceof RankAccessible)) return !role1.isEveryoneRole();
                RankAccessible cmd = (RankAccessible) command;
                return role1.getPosition() != cmd.getMinimumRank();

            });
            StringBuilder builder = new StringBuilder();
            roleCommands.forEach(command -> {
                if (!(command instanceof MainCommand && subcommands)) {
                    builder.append("**").append(command.getUsage()).append("** -> ").append(command.getDescription()).append("\n");
                } else {
                    mainCommands.add(command);
                }
            });
            if (!builder.toString().isEmpty())
                msg.appendField(new Embed.EmbedField((role1.getPosition() != 0) ? role1.getName() : "Everyone", builder.toString(), false));
        });
        mainCommands.forEach(command -> {
            StringBuilder builder = new StringBuilder();
            builder.append("**").append(command.getUsage()).append("** -> ").append(command.getDescription()).append("\n");
            if (command instanceof MainCommand && subcommands) {
                MainCommand mainCommand = (MainCommand) command;
                mainCommand.getSubcommands().forEach(subcommand -> builder.append("    - **").append(subcommand.getUsage().replace(subcommand.getPrefix(), "")).append("** -> ").append(subcommand.getDescription()).append("\n"));
            }
            String cmdName = command.getCommand();
            String firstLetter = ((Character) cmdName.charAt(0)).toString();
            cmdName = cmdName.replaceFirst(firstLetter, firstLetter.toUpperCase());
            if (!builder.toString().isEmpty())
                msg.appendField(new Embed.EmbedField(cmdName, builder.toString(), false));
        });
    }
}