package nl.underkoen.discordbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.entities.DChannel;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.entities.DServer;
import nl.underkoen.discordbot.utils.Messages.TextMessage;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class MusicHandler {
    public static AudioPlayerManager playerManager;
    public static Map<Long, GuildMusicManager> musicManagers;

    public static DChannel channel;

    public MusicHandler() {
        musicManagers = new HashMap<>();
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public static synchronized GuildMusicManager getGuildAudioPlayer(DServer server) {
        long guildId = server.getGuild().getLongID();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        server.getGuild().getAudioManager().setAudioProvider(musicManager.getAudioProvider());

        return musicManager;
    }

    public void skipTrack(DServer server) {
        GuildMusicManager musicManager = getGuildAudioPlayer(server);
        musicManager.scheduler.nextTrack();
    }

    public void playTrack(GuildMusicManager musicManager, AudioTrack track, DContext context) {
        musicManager.scheduler.queue(track, context);
    }

    public void joinChannel(IVoiceChannel channel) {
        channel.join();
    }

    public void leave(DServer server) {
        server.getGuild().getConnectedVoiceChannel().leave();
        getGuildAudioPlayer(server).scheduler.clearQueue();
        getGuildAudioPlayer(server).player.stopTrack();
    }

    public static boolean isPlayingMusic(DServer server) {
        return getCurrentTrack(server) != null;
    }

    public static boolean isPlayingDefaultMusic(DServer server) {
        return getCurrentTrack(server) == getGuildAudioPlayer(server).scheduler.defaultTrack;
    }

    public static boolean hasDefaultMusic(DServer server) {
        return getGuildAudioPlayer(server).scheduler.defaultTrack != null;
    }

    public void setDefaultTrack(GuildMusicManager musicManager, AudioTrack track) {
        musicManager.scheduler.setDefault(track);
    }

    public static AudioTrack getCurrentTrack(DServer server) {
        return getGuildAudioPlayer(server).player.getPlayingTrack();
    }

    public static AudioTrack getDefaultTrack(DServer server) {
        return getGuildAudioPlayer(server).scheduler.defaultTrack;
    }

    public static void setVolume(DServer server, int volume) {
        getGuildAudioPlayer(server).player.setVolume(volume);
    }

    public static int getVolume(DServer server) {
        return getGuildAudioPlayer(server).player.getVolume();
    }

    public static AudioTrack[] getQueue(DServer server) {
        return getGuildAudioPlayer(server).scheduler.getQueue();
    }

    @EventSubscriber
    public void onGuildVoiceJoin(UserVoiceChannelLeaveEvent event) {
        DServer server = DServer.getServer(event.getGuild());
        if (!isPlayingMusic(server)) return;
        IVoiceChannel channel = event.getVoiceChannel();
        if (DMember.getMember(server, DiscordBot.client.getOurUser()).getVoiceState().getChannel() == channel) {
            getGuildAudioPlayer(server).scheduler.setPause(false);
            if (channel.getConnectedUsers().size() == 2) {
                new TextMessage().addText("Just unpaused: [" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().title + "](" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().uri + ")").sendMessage(MusicHandler.channel);
            }
        }
    }

    @EventSubscriber
    public void onGuildVoiceLeave(UserVoiceChannelLeaveEvent event) {
        DServer server = DServer.getServer(event.getGuild());
        if (!isPlayingMusic(server)) return;
        IVoiceChannel channel = event.getVoiceChannel();
        if (DMember.getMember(server, DiscordBot.client.getOurUser()).getVoiceState().getChannel() == channel) {
            if (channel.getConnectedUsers().size() == 1) {
                getGuildAudioPlayer(server).scheduler.setPause(true);
                new TextMessage().addText("Just paused: [" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().title + "](" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().uri + ")").sendMessage(MusicHandler.channel);
            }
        }
    }

    @EventSubscriber
    public void onGuildVoiceMove(UserVoiceChannelMoveEvent event) {
        DServer server = DServer.getServer(event.getGuild());
        if (!isPlayingMusic(server)) return;
        IVoiceChannel channel = event.getOldChannel();
        if (DMember.getMember(server, DiscordBot.client.getOurUser()).getVoiceState().getChannel() == channel) {
            if (channel.getConnectedUsers().size() == 1) {
                getGuildAudioPlayer(server).scheduler.setPause(true);
                new TextMessage().addText("Just paused: [" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().title + "](" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().uri + ")").sendMessage(MusicHandler.channel);
            }
        } else if (DMember.getMember(server, DiscordBot.client.getOurUser()).getVoiceState().getChannel() == event.getNewChannel()) {
            getGuildAudioPlayer(server).scheduler.setPause(false);
            if (event.getNewChannel().getConnectedUsers().size() == 2) {
                new TextMessage().addText("Just unpaused: [" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().title + "](" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().uri + ")").sendMessage(MusicHandler.channel);
            }
        }
    }
}
