package nl.underkoen.discordbot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import nl.underkoen.discordbot.Main;
import nl.underkoen.discordbot.Roles;
import nl.underkoen.discordbot.commands.Command;
import nl.underkoen.discordbot.entities.CommandContext;
import nl.underkoen.discordbot.music.GuildMusicManager;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 11-05-17.
 */
public class DefaultCommand implements Command {
    private String command = "default";
    private String usage = "default [url]";
    private String description = "This song will be played when there is no other music";

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
        if (context.getArgs().length == 0) {
            new ErrorMessage(context.getMember(), "This command needs arguments to work").sendMessage(context.getChannel());
            return;
        }
        if (Main.getSelfMember(context.getGuild()).getVoiceState().getChannel() == null) {
            new ErrorMessage(context.getMember(), "Bot needs to be in a voice channel").sendMessage(context.getChannel());
            return;
        }
        GuildMusicManager musicManager = MusicCommand.musicHandler.getGuildAudioPlayer(context.getGuild());

        String url = context.getRawArgs()[0];

        if (url.contentEquals("null")) {
            MusicCommand.musicHandler.setDefaultTrack(context.getGuild(), musicManager, null);
            new TextMessage().setMention(context.getMember()).addText("Cleared the default track").sendMessage(context.getChannel());
            return;
        }

        MusicCommand.musicHandler.playerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                MusicCommand.musicHandler.setDefaultTrack(context.getGuild(), musicManager, track);
                new TextMessage().setMention(context.getMember()).addText("Set the default track to [" + track.getInfo().title + "](" + track.getInfo().uri + ")").sendMessage(context.getChannel());
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                new ErrorMessage(context.getMember(), "Playlist support not added yet").sendMessage(context.getChannel());
            }

            @Override
            public void noMatches() {
                new ErrorMessage(context.getMember(), "Couldn't find " + url).sendMessage(context.getChannel());
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                new ErrorMessage(context.getMember(), "The load of " + url + " was not succesfull.").sendMessage(context.getChannel());
            }
        });
    }
}