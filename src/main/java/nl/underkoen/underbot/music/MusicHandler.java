package nl.underkoen.underbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.entities.impl.MemberImpl;
import nl.underkoen.underbot.utils.Messages.TextMessage;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelLeaveEvent;
import sx.blah.discord.handle.impl.events.guild.voice.user.UserVoiceChannelMoveEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class MusicHandler {
    public static AudioPlayerManager playerManager;
    public static Map<Long, GuildMusicManager> musicManagers;

    public static IChannel channel;

    public MusicHandler() {
        this.musicManagers = new HashMap<>();
        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public static synchronized GuildMusicManager getGuildAudioPlayer(IGuild guild) {
        long guildId = guild.getLongID();
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setAudioProvider(musicManager.getAudioProvider());

        return musicManager;
    }

    public void skipTrack(IGuild guild) {
        GuildMusicManager musicManager = getGuildAudioPlayer(guild);
        musicManager.scheduler.nextTrack();
    }

    public void playTrack(IGuild guild, GuildMusicManager musicManager, AudioTrack track, CommandContext context) {
        musicManager.scheduler.queue(track, context);
    }

    public void joinChannel(IVoiceChannel channel) {
        channel.join();
    }

    public void leave(IGuild guild) {
        guild.getConnectedVoiceChannel().leave();
        getGuildAudioPlayer(guild).scheduler.clearQueue();
        getGuildAudioPlayer(guild).player.stopTrack();
    }

    public static boolean isPlayingMusic(IGuild guild) {
        return getCurrentTrack(guild) != null;
    }

    public static boolean isPlayingDefaultMusic(IGuild guild) {
        return getCurrentTrack(guild) == getGuildAudioPlayer(guild).scheduler.defaultTrack;
    }

    public static boolean hasDefaultMusic(IGuild guild) {
        return getGuildAudioPlayer(guild).scheduler.defaultTrack != null;
    }

    public void setDefaultTrack(IGuild guild, GuildMusicManager musicManager, AudioTrack track) {
        musicManager.scheduler.setDefault(track);
    }

    public static AudioTrack getCurrentTrack(IGuild guild) {
        return getGuildAudioPlayer(guild).player.getPlayingTrack();
    }

    public static AudioTrack getDefaultTrack(IGuild guild) {
        return getGuildAudioPlayer(guild).scheduler.defaultTrack;
    }

    public static void setVolume(IGuild guild, int volume) {
        getGuildAudioPlayer(guild).player.setVolume(volume);
    }

    public static int getVolume(IGuild guild) {
        return getGuildAudioPlayer(guild).player.getVolume();
    }

    public static AudioTrack[] getQueue(IGuild guild) {
        return getGuildAudioPlayer(guild).scheduler.getQueue();
    }

    @EventSubscriber
    public void onGuildVoiceJoin(UserVoiceChannelLeaveEvent event) {
        IGuild guild = event.getGuild();
        if (!isPlayingMusic(guild)) return;
        IVoiceChannel channel = event.getVoiceChannel();
        if (new MemberImpl(guild, Main.client.getOurUser()).getVoiceState().getChannel() == channel) {
            getGuildAudioPlayer(guild).scheduler.setPause(false);
            if (channel.getConnectedUsers().size() == 2) {
                new TextMessage().addText("Just unpaused: [" + getGuildAudioPlayer(guild).player.getPlayingTrack().getInfo().title + "](" + getGuildAudioPlayer(guild).player.getPlayingTrack().getInfo().uri + ")").sendMessage(this.channel);
            }
        }
    }

    @EventSubscriber
    public void onGuildVoiceLeave(UserVoiceChannelLeaveEvent event) {
        IGuild guild = event.getGuild();
        if (!isPlayingMusic(guild)) return;
        IVoiceChannel channel = event.getVoiceChannel();
        if (new MemberImpl(guild, Main.client.getOurUser()).getVoiceState().getChannel() == channel) {
            if (channel.getConnectedUsers().size() == 1) {
                getGuildAudioPlayer(guild).scheduler.setPause(true);
                new TextMessage().addText("Just paused: [" + getGuildAudioPlayer(guild).player.getPlayingTrack().getInfo().title + "](" + getGuildAudioPlayer(guild).player.getPlayingTrack().getInfo().uri + ")").sendMessage(this.channel);
            }
        }
    }

    @EventSubscriber
    public void onGuildVoiceMove(UserVoiceChannelMoveEvent event) {
        IGuild guild = event.getGuild();
        if (!isPlayingMusic(guild)) return;
        IVoiceChannel channel = event.getOldChannel();
        if (new MemberImpl(guild, Main.client.getOurUser()).getVoiceState().getChannel() == channel) {
            if (channel.getConnectedUsers().size() == 1) {
                getGuildAudioPlayer(guild).scheduler.setPause(true);
                new TextMessage().addText("Just paused: [" + getGuildAudioPlayer(guild).player.getPlayingTrack().getInfo().title + "](" + getGuildAudioPlayer(guild).player.getPlayingTrack().getInfo().uri + ")").sendMessage(this.channel);
            }
        } else if (new MemberImpl(guild, Main.client.getOurUser()).getVoiceState().getChannel() == event.getNewChannel()) {
            getGuildAudioPlayer(guild).scheduler.setPause(false);
            if (event.getNewChannel().getConnectedUsers().size() == 2) {
                new TextMessage().addText("Just unpaused: [" + getGuildAudioPlayer(guild).player.getPlayingTrack().getInfo().title + "](" + getGuildAudioPlayer(guild).player.getPlayingTrack().getInfo().uri + ")").sendMessage(this.channel);
            }
        }
    }
}
