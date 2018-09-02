package nl.underkoen.discordbot.music.commands;

import nl.underkoen.chatbot.models.RankAccessible;
import nl.underkoen.discordbot.Roles;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.music.MusicHandler;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class JoinCommand implements DCommand, RankAccessible {
    private String command = "join";
    private String usage = "join";
    private String description = "Let the bot join your channel";
    private String[] aliases = {"j"};

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
        return Roles.MOD.role;
    }

    @Override
    public void trigger(DContext context) {
        DMember member = context.getMember();
        if (member.getVoiceState().getChannel() == null) {
            new ErrorMessage(member, "You need to be in a voice channel").sendMessage(context.getChannel());
            return;
        }
        MusicHandler.channel = context.getChannel();
        new TextMessage().addText("Just joined: " + context.getMember()
                .getVoiceState().getChannel().getName()).setMention(context.getMember()).sendMessage(context.getChannel());
        MusicCommand.musicHandler.joinChannel(context.getMember().getVoiceState().getChannel());
    }
}