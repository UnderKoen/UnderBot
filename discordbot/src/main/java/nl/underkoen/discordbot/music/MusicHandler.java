package nl.underkoen.discordbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.entities.DChannel;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.entities.DServer;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class MusicHandler implements EventListener {
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
        long guildId = server.getGuild().getIdLong();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        server.getGuild().getAudioManager().setSendingHandler(musicManager.getAudioProvider());

        return musicManager;
    }

    public void skipTrack(DServer server) {
        GuildMusicManager musicManager = getGuildAudioPlayer(server);
        musicManager.scheduler.nextTrack();
    }

    public void playTrack(GuildMusicManager musicManager, AudioTrack track, DContext context) {
        musicManager.scheduler.queue(track, context);
    }

    public void joinChannel(VoiceChannel channel) {
        channel.getGuild().getAudioManager().openAudioConnection(channel);
    }

    public void leave(DServer server) {
        server.getGuild().getAudioManager().closeAudioConnection();
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

    @Override
    public void onEvent(Event event) {
        if (event instanceof GuildVoiceJoinEvent) {
            GuildVoiceJoinEvent joinEvent = (GuildVoiceJoinEvent) event;
            onGuildVoiceJoin(joinEvent);
        } else if (event instanceof GuildVoiceLeaveEvent) {
            GuildVoiceLeaveEvent leaveEvent = (GuildVoiceLeaveEvent) event;
            onGuildVoiceLeave(leaveEvent);
        } else if (event instanceof GuildVoiceMoveEvent) {
            GuildVoiceMoveEvent moveEvent = (GuildVoiceMoveEvent) event;
            onGuildVoiceMove(moveEvent);
        }
    }

    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        DServer server = DServer.getServer(event.getGuild());
        if (!isPlayingMusic(server)) return;
        VoiceChannel channel = event.getChannelJoined();
        if (DMember.getMember(server, DiscordBot.client.getSelfUser()).getVoiceState().getAudioChannel() == channel) {
            getGuildAudioPlayer(server).scheduler.setPause(false);
            if (channel.getMembers().size() == 2) {
                new TextMessage().addText("Just unpaused: [" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().title + "](" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().uri + ")").sendMessage(MusicHandler.channel);
            }
        }
    }

    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        DServer server = DServer.getServer(event.getGuild());
        if (!isPlayingMusic(server)) return;
        VoiceChannel channel = event.getChannelLeft();
        if (DMember.getMember(server, DiscordBot.client.getSelfUser()).getVoiceState().getAudioChannel() == channel) {
            if (channel.getMembers().size() == 1) {
                getGuildAudioPlayer(server).scheduler.setPause(true);
                new TextMessage().addText("Just paused: [" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().title + "](" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().uri + ")").sendMessage(MusicHandler.channel);
            }
        }
    }

    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        DServer server = DServer.getServer(event.getGuild());
        if (!isPlayingMusic(server)) return;
        VoiceChannel channel = event.getChannelLeft();
        if (DMember.getMember(server, DiscordBot.client.getSelfUser()).getVoiceState().getAudioChannel() == channel) {
            if (channel.getMembers().size() == 1) {
                getGuildAudioPlayer(server).scheduler.setPause(true);
                new TextMessage().addText("Just paused: [" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().title + "](" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().uri + ")").sendMessage(MusicHandler.channel);
            }
        } else if (DMember.getMember(server, DiscordBot.client.getSelfUser()).getVoiceState().getAudioChannel() == event.getChannelJoined()) {
            getGuildAudioPlayer(server).scheduler.setPause(false);
            if (event.getChannelJoined().getMembers().size() == 2) {
                new TextMessage().addText("Just unpaused: [" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().title + "](" + getGuildAudioPlayer(server).player.getPlayingTrack().getInfo().uri + ")").sendMessage(MusicHandler.channel);
            }
        }
    }
}
