package nl.underkoen.discordbot.music.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.Roles;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.music.GuildMusicManager;
import nl.underkoen.discordbot.music.MusicHandler;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;
import nl.underkoen.discordbot.utils.RoleUtil;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class PlayCommand implements DCommand {
    private String command = "play";
    private String usage = "play [url]";
    private String description = "Let the bot play the song";
    private String[] aliases = {"p"};

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
    public void trigger(DContext context) {
        if (context.getArgs().length == 0) {
            new ErrorMessage(context.getMember(), "This command needs arguments to work").sendMessage(context.getChannel());
            return;
        }
        if (DiscordBot.getSelfMember(context.getServer()).getVoiceState().getAudioChannel() == null) {
            new ErrorMessage(context.getMember(), "Bot needs to be in a voice channel").sendMessage(context.getChannel());
            return;
        }
        GuildMusicManager musicManager = MusicHandler.getGuildAudioPlayer(context.getServer());

        String url = context.getRawArgs()[0];

        MusicHandler.playerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                int role = RoleUtil.getHighestRole(context.getMember()).getPosition();
                int durationInMinutes = Math.round(track.getDuration() / 1000 / 60);
                if (role < Roles.MOD.role) {
                    if (MusicHandler.getQueue(context.getServer()).length >= 10) {
                        new TextMessage().setMention(context.getMember()).addText("You can't add anymore songs queue is full").sendMessage(context.getChannel());
                        return;
                    }
                    if (durationInMinutes > ((role == Roles.SUPPORTER.role) ? 15 : 5)) {
                        new TextMessage().setMention(context.getMember()).addText(durationInMinutes + " minutes is to long for your role.").sendMessage(context.getChannel());
                        return;
                    }
                }
                MusicCommand.musicHandler.playTrack(musicManager, track, context);
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