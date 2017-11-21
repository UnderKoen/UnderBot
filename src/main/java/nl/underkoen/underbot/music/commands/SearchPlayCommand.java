package nl.underkoen.underbot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.Roles;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.music.GuildMusicManager;
import nl.underkoen.underbot.music.MusicHandler;
import nl.underkoen.underbot.utils.Messages.ErrorMessage;
import nl.underkoen.underbot.utils.Messages.TextMessage;
import nl.underkoen.underbot.utils.RoleUtil;
import nl.underkoen.underbot.utils.YoutubeUtil;

/**
 * Created by Under_Koen on 10/10/2017.
 */
public class SearchPlayCommand implements Command {
    private String command = "searchplay";
    private String usage = "searchplay [search...]";
    private String description = "Searches on youtube for a song";
    private String[] aliases = {"sp"};

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

        StringBuilder str = new StringBuilder();

        for (String s : context.getRawArgs()) {
            str.append(s);
        }

        String url = YoutubeUtil.getYoutubeVideo(str.toString());

        MusicCommand.musicHandler.playerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                int role = RoleUtil.getHighestRole(context.getMember()).getPosition();
                int durationInMinutes = Math.round(track.getDuration() / 1000 / 60);
                if (role < Roles.MOD.role) {
                    if (MusicHandler.getQueue(context.getGuild()).length >= 10) {
                        new TextMessage().setMention(context.getMember()).addText("You can't add anymore songs queue is full").sendMessage(context.getChannel());
                        return;
                    }
                    if (durationInMinutes > ((role == Roles.SUPPORTER.role) ? 15 : 5)) {
                        new TextMessage().setMention(context.getMember()).addText(durationInMinutes + " minutes is to long for your role.").sendMessage(context.getChannel());
                        return;
                    }
                }
                MusicCommand.musicHandler.playTrack(context.getGuild(), musicManager, track, context);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                new ErrorMessage(context.getMember(), "Use /music playlist for playing a playlist").sendMessage(context.getChannel());
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