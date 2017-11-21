package nl.underkoen.underbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.music.commands.NextCommand;
import nl.underkoen.underbot.utils.Messages.TextMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public AudioTrack[] getQueue() {
        return queue.toArray(new AudioTrack[0]);
    }

    public void queue(AudioTrack track, CommandContext context) {
        if (player.getPlayingTrack() == defaultTrack && defaultTrack != null) {
            player.startTrack(null, false);
        }
        if (!player.startTrack(track, true)) {
            queue.offer(track);
            new TextMessage().setMention(context.getMember()).addText("Queued [" + track.getInfo().title + "](" + track.getInfo().uri + ") as " + queue.size() + "th").sendMessage(context.getChannel());
        } else {
            new TextMessage().setMention(context.getMember()).addText("Started playing [" + track.getInfo().title + "](" + track.getInfo().uri + ")").sendMessage(context.getChannel());
        }
    }

    public void clearQueue() {
        queue.clear();
    }

    public AudioTrack defaultTrack;

    public void setDefault(AudioTrack defaultTrack) {
        this.defaultTrack = defaultTrack;
    }

    public void nextTrack() {
        if (queue.isEmpty() && defaultTrack == null && player.getPlayingTrack() == null) return;
        AudioTrack track;
        if (queue.isEmpty() && defaultTrack != null) {
            track = defaultTrack.makeClone();
            defaultTrack = track;
            player.startTrack(track, true);
        } else {
            track = queue.poll();
        }
        player.startTrack(track, false);
        if (track != null) {
            new TextMessage().addText("Started playing [" + track.getInfo().title + "](" + track.getInfo().uri + ")").sendMessage(MusicHandler.channel);
        }
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        NextCommand.votes.clear();
        if (endReason.mayStartNext && (!queue.isEmpty() || defaultTrack != null)) {
            nextTrack();
        }
    }

    public void setPause(boolean bool) {
        player.setPaused(bool);
    }

    public boolean isPaused() {
        return player.isPaused();
    }
}
