package nl.underkoen.underbot.utils.Messages;

import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.commands.MainCommand;
import nl.underkoen.underbot.entities.Member;
import nl.underkoen.underbot.utils.ColorUtil;
import nl.underkoen.underbot.utils.RoleUtil;
import sx.blah.discord.handle.impl.obj.Embed;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Under_Koen on 21-04-17.
 */
public class HelpMessage implements UnderMessage {

    private Color color = ColorUtil.hexToColor("#2a6886");

    private Member user;

    private String message;

    private boolean subcommands;

    private List<Command> commands;

    public HelpMessage setMention(Member user) {
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

    public HelpMessage addCommand(Command command) {
        if (this.commands == null) this.commands = new ArrayList<>();
        this.commands.add(command);
        return this;
    }

    public HelpMessage addCommands(List<Command> commands) {
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
    public Member getAuthor() {
        return user;
    }

    @Override
    public String getDescription() {
        return message;
    }

    @Override
    public void sendMessage(IChannel channel) {

        EmbedBuilder msg = new EmbedBuilder();

        Color color = getColor();
        if (color != null) {
            msg.withColor(color);
        }

        Member author = getAuthor();
        if (author != null) {
            msg.withFooterText(author.getEffectiveName());
            msg.withFooterIcon(author.getUser().getAvatarURL());
        }

        String desc = getDescription();
        if (desc != null) {
            msg.withDescription(desc);
        }

        int role = RoleUtil.getHighestRole(channel.getGuild()).getPosition();
        if (author != null) {
            role = RoleUtil.getHighestRole(author).getPosition();
        }

        List<IRole> roles = new ArrayList<>(channel.getGuild().getRoles());
        int finalRole = role;
        List<Command> mainCommands = new ArrayList<>();
        roles.forEach(role1 -> {
            if (role1.getPosition() > finalRole) return;
            List<Command> roleCommands = new ArrayList<>(commands);
            roleCommands.removeIf(command -> role1.getPosition() != command.getMinimumRole());
            StringBuilder builder = new StringBuilder();
            roleCommands.forEach(command -> {
                if (!(command instanceof MainCommand && subcommands)) {
                    builder.append("**" + command.getUsage() + "** -> " + command.getDescription() + "\n");
                } else {
                    mainCommands.add(command);
                }
            });
            if (!builder.toString().isEmpty()) msg.appendField(new Embed.EmbedField((role1.getPosition() != 0) ? role1.getName() : "Everyone", builder.toString(), false));
        });
        mainCommands.forEach(command -> {
            StringBuilder builder = new StringBuilder();
            builder.append("**" + command.getUsage() + "** -> " + command.getDescription() + "\n");
            if (command instanceof MainCommand && subcommands) {
                MainCommand mainCommand = (MainCommand) command;
                mainCommand.getSubcommands().forEach(subcommand -> {
                    builder.append("    - **" + subcommand.getUsage().replace(Main.handler.getPrefix(),"") + "** -> " + subcommand.getDescription() + "\n");
                });
            }
            if (!builder.toString().isEmpty()) msg.appendField(new Embed.EmbedField(command.getCommand(), builder.toString(), false));
        });

        IMessage ms = channel.sendMessage(msg.build());

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        ms.delete();
                    }
                },
                TimeUnit.MINUTES.toMillis(5)
        );
    }
}