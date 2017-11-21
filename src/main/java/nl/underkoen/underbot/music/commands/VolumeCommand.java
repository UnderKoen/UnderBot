package nl.underkoen.underbot.music.commands;

import nl.underkoen.underbot.Roles;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.music.MusicHandler;
import nl.underkoen.underbot.utils.Messages.ErrorMessage;
import nl.underkoen.underbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class VolumeCommand implements Command {
    private String command = "volume";
    private String usage = "volume";
    private String description = "Set the bot volume.";
    private String[] aliases = {"v"};

    @Override
    public String[] getAliases() {
        return aliases;
    }
    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getMinimumRole() {
        return Roles.SUPPORTER.role;
    }

    @Override
    public void setup() throws Exception {
    }

    @Override
    public void run(CommandContext context) {
        if (context.getArgs().length == 0) {
            new TextMessage().addText("The volume is " + MusicHandler.getVolume(context.getGuild()) + "%").setMention(context.getMember()).sendMessage(context.getChannel());
            return;
        }
        int volume = 0;
        try {
            volume = Integer.parseInt(context.getArgs()[0]);
        } catch (Exception e) {
            new ErrorMessage(context.getMember(), context.getArgs()[0] + " is no valid integer").sendMessage(context.getChannel());
            return;
        }
        new TextMessage().addText("Set volume to " + volume + "%").setMention(context.getMember()).sendMessage(context.getChannel());
        MusicHandler.setVolume(context.getGuild(), volume);
    }
}