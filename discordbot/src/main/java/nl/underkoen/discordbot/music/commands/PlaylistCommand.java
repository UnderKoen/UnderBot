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
import nl.underkoen.discordbot.music.MusicHandler;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;
import nl.underkoen.discordbot.utils.RoleUtil;

import java.util.List;
import java.util.Random;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class PlaylistCommand implements Command {
    private String command = "playlist";
    private String usage = "playlist [url] [amount]";
    private String description = "Plays random [amount] of songs from the url";

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
        if (context.getArgs().length < 2) {
            new ErrorMessage(context.getMember(), "This command needs more arguments to work").sendMessage(context.getChannel());
            return;
        }
        if (Main.getSelfMember(context.getGuild()).getVoiceState().getChannel() == null) {
            new ErrorMessage(context.getMember(), "Bot needs to be in a voice channel").sendMessage(context.getChannel());
            return;
        }
        GuildMusicManager musicManager = MusicCommand.musicHandler.getGuildAudioPlayer(context.getGuild());

        String url = context.getRawArgs()[0];

        MusicCommand.musicHandler.playerManager.loadItemOrdered(musicManager, url, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                new ErrorMessage(context.getMember(), "Use /music play for playing a normal video").sendMessage(context.getChannel());
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                List<AudioTrack> tracks = playlist.getTracks();

                int amount = 0;
                try {
                    amount = Integer.parseInt(context.getArgs()[1]);
                } catch (Exception e) {
                    new ErrorMessage(context.getMember(), context.getArgs()[0] + " is no valid integer").sendMessage(context.getChannel());
                    return;
                }
                int role = RoleUtil.getHighestRole(context.getMember()).getPosition();
                if (role < Roles.MOD.role) {
                    if (MusicHandler.getQueue(context.getGuild()).length >= 10) {
                        new TextMessage().setMention(context.getMember()).addText("You can't add anymore songs queue is full").sendMessage(context.getChannel());
                        return;
                    }
                    if (amount > ((role == Roles.SUPPORTER.role) ? 10 : 5)) {
                        new TextMessage().setMention(context.getMember()).addText(amount + " is to much for you role.").sendMessage(context.getChannel());
                        return;
                    }
                }

                if (tracks.size() < amount) {
                    new TextMessage().setMention(context.getMember()).addText("This playlist does not contain enough songs.").sendMessage(context.getChannel());
                    return;
                }

                for (int i = 0; i < amount; i++) {
                    if (role < Roles.MOD.role && MusicHandler.getQueue(context.getGuild()).length >= 10) {
                        new TextMessage().setMention(context.getMember()).addText("You can't add anymore songs queue is full").sendMessage(context.getChannel());
                        return;
                    }
                    AudioTrack track = tracks.remove(new Random().nextInt(tracks.size()));
                    int durationInMinutes = Math.round(track.getDuration() / 1000 / 60);
                    if (durationInMinutes > ((role >= Roles.SUPPORTER.role) ? 10 : 5)) {
                        new TextMessage().setMention(context.getMember()).addText("Did not add [" + track.getInfo().title + "](" + track.getInfo().uri + ")" + " because  " + durationInMinutes + " minutes is to long").sendMessage(context.getChannel());
                        continue;
                    }
                    MusicCommand.musicHandler.playTrack(context.getGuild(), musicManager, track, context);
                }
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