package nl.underkoen.discordbot.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import nl.underkoen.chatbot.models.RankAccessible;
import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.Roles;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.music.MusicHandler;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class ForceNextCommand implements DCommand, RankAccessible {
    private String command = "forcenext";
    private String usage = "forcenext";
    private String description = "Let the bot skip this song.";

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
        if (context.getMember().getVoiceState().getChannel() != DiscordBot.getSelfMember(context.getServer()).getVoiceState().getChannel()) {
            new ErrorMessage(context.getMember(), "You need to be in " + DiscordBot.getSelfMember(context.getServer()).getVoiceState().getChannel().getName()).sendMessage(context.getChannel());
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
        AudioTrack track = MusicHandler.getCurrentTrack(context.getServer());
        new TextMessage().setMention(context.getMember()).addText("Skipped [" + track.getInfo().title + "](" + track.getInfo().uri + ")").sendMessage(context.getChannel());
        MusicCommand.musicHandler.skipTrack(context.getServer());
    }
}