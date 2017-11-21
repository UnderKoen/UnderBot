package nl.underkoen.underbot.music.commands;

import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.Roles;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.entities.Member;
import nl.underkoen.underbot.utils.Messages.ErrorMessage;
import nl.underkoen.underbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class LeaveCommand implements Command {
    private String command = "leave";
    private String usage = "leave";
    private String description = "Let the bot leave the channel.";
    private String[] aliases = {"l"};

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
        if (Main.getSelfMember(context.getGuild()).getVoiceState().getChannel() == null) {
            new ErrorMessage(member, "The bot needs to be in a voice channel").sendMessage(context.getChannel());
            return;
        }
        new TextMessage().addText("Just leaved: " + context.getMember()
                .getVoiceState().getChannel().getName()).setMention(context.getMember()).sendMessage(context.getChannel());
        MusicCommand.musicHandler.leave(context.getGuild());
    }
}