package nl.underkoen.discordbot.music.commands;

import nl.underkoen.chatbot.models.RankAccessible;
import nl.underkoen.discordbot.Roles;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.music.MusicHandler;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class VolumeCommand implements DCommand, RankAccessible {
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
    public int getMinimumRank() {
        return Roles.SUPPORTER.role;
    }

    @Override
    public void trigger(DContext context) {
        if (context.getArgs().length == 0) {
            new TextMessage().addText("The volume is " + MusicHandler.getVolume(context.getServer()) + "%").setMention(context.getMember()).sendMessage(context.getChannel());
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
        MusicHandler.setVolume(context.getServer(), volume);
    }
}