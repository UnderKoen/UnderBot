package nl.underkoen.discordbot.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import nl.underkoen.discordbot.Main;
import nl.underkoen.discordbot.commands.Command;
import nl.underkoen.discordbot.entities.CommandContext;
import nl.underkoen.discordbot.music.MusicHandler;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 12-05-17.
 */
public class NextCommand implements Command {
    private String command = "next";
    private String usage = "next";
    private String description = "Vote to skip this song.";
    private String[] aliases = {"skip", "n", "s"};

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
    public void setup() throws Exception {
    }

    public static List<IUser> votes = new ArrayList();

    @Override
    public void run(CommandContext context) {
        if (context.getMember().getVoiceState().getChannel() != Main.getSelfMember(context.getGuild()).getVoiceState().getChannel()) {
            new ErrorMessage(context.getMember(), "You need to be in " + Main.getSelfMember(context.getGuild()).getVoiceState().getChannel().getName()).sendMessage(context.getChannel());
            return;
        }
        if (!MusicHandler.isPlayingMusic(context.getGuild())) {
            new ErrorMessage(context.getMember(), "Bot isn't playing music").sendMessage(context.getChannel());
            return;
        }
        if (MusicHandler.isPlayingDefaultMusic(context.getGuild())) {
            new ErrorMessage(context.getMember(), "Can't skip default song").sendMessage(context.getChannel());
            return;
        }
        if (votes.contains(context.getUser())) {
            new ErrorMessage(context.getMember(), "You already voted").sendMessage(context.getChannel());
            return;
        }
        votes.add(context.getUser());
        AudioTrack track = MusicHandler.getCurrentTrack(context.getGuild());
        new TextMessage().setMention(context.getMember()).addText(context.getMember().getEffectiveName() +
                " voted to skip [" + track.getInfo().title + "](" + track.getInfo().uri + ") (" +
                votes.size() + "/" + (Math.round(Double.parseDouble(context.getMember().getVoiceState().getChannel().getConnectedUsers().size() + "") / 2.0) + ")"))
                .sendMessage(context.getChannel());
        if (votes.size() >= (Math.round(Double.parseDouble(context.getMember().getVoiceState().getChannel().getConnectedUsers().size() + "") / 2.0))) {
            new TextMessage().addText("Skipped [" + track.getInfo().title + "](" + track.getInfo().uri + ")").sendMessage(context.getChannel());
            MusicCommand.musicHandler.skipTrack(context.getGuild());
        }
    }
}
