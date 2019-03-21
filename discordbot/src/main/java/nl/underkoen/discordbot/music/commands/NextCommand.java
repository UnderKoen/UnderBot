package nl.underkoen.discordbot.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.VoiceChannel;
import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.entities.DUser;
import nl.underkoen.discordbot.music.MusicHandler;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 12-05-17.
 */
public class NextCommand implements DCommand {
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

    public static List<DUser> votes = new ArrayList<>();

    @Override
    public void trigger(DContext context) {
        if (context.getMember().getVoiceState().getAudioChannel() != DiscordBot.getSelfMember(context.getServer()).getVoiceState().getAudioChannel()) {
            new ErrorMessage(context.getMember(), "You need to be in " + ((VoiceChannel) DiscordBot.getSelfMember(context.getServer()).getVoiceState().getAudioChannel()).getName()).sendMessage(context.getChannel());
            return;
        }
        if (!MusicHandler.isPlayingMusic(context.getServer())) {
            new ErrorMessage(context.getMember(), "Bot isn't playing music").sendMessage(context.getChannel());
            return;
        }
        if (MusicHandler.isPlayingDefaultMusic(context.getServer())) {
            new ErrorMessage(context.getMember(), "Can't skip default song").sendMessage(context.getChannel());
            return;
        }
        if (votes.contains(context.getUser())) {
            new ErrorMessage(context.getMember(), "You already voted").sendMessage(context.getChannel());
            return;
        }
        votes.add(context.getUser());
        AudioTrack track = MusicHandler.getCurrentTrack(context.getServer());
        new TextMessage().setMention(context.getMember()).addText(context.getMember().getEffectiveName() +
                " voted to skip [" + track.getInfo().title + "](" + track.getInfo().uri + ") (" +
                votes.size() + "/" + (Math.round(Double.parseDouble(((VoiceChannel) context.getMember().getVoiceState().getAudioChannel()).getMembers().size() + "") / 2.0) + ")"))
                .sendMessage(context.getChannel());
        if (votes.size() >= (Math.round(Double.parseDouble(((VoiceChannel) context.getMember().getVoiceState().getAudioChannel()).getMembers().size() + "") / 2.0))) {
            new TextMessage().addText("Skipped [" + track.getInfo().title + "](" + track.getInfo().uri + ")").sendMessage(context.getChannel());
            MusicCommand.musicHandler.skipTrack(context.getServer());
        }
    }
}
