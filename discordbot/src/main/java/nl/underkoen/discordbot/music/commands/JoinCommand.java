package nl.underkoen.discordbot.music.commands;

import nl.underkoen.discordbot.Roles;
import nl.underkoen.discordbot.commands.Command;
import nl.underkoen.discordbot.entities.CommandContext;
import nl.underkoen.discordbot.entities.Member;
import nl.underkoen.discordbot.music.MusicHandler;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class JoinCommand implements Command {
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
    public int getMinimumRole() {
        return Roles.MOD.role;
    }

    @Override
    public void setup() throws Exception {
    }

    @Override
    public void run(CommandContext context) {
        Member member = context.getMember();
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