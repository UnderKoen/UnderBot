package nl.underkoen.discordbot.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.music.MusicHandler;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class QueueCommand implements DCommand {
    private String command = "queue";
    private String usage = "queue";
    private String description = "Outputs the queue of the bot";
    private String[] aliases = {"q"};

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
        AudioTrack[] tracks = MusicHandler.getQueue(context.getServer());
        AudioTrack current = MusicHandler.getCurrentTrack(context.getServer());
        if (tracks.length == 0 && current == null) {
            new TextMessage().setMention(context.getMember()).addText("The queue is empty").sendMessage(context.getChannel());
            return;
        }
        TextMessage msg = new TextMessage().setMention(context.getMember()).addText("The queue is:");
        if (MusicHandler.hasDefaultMusic(context.getServer())) {
            AudioTrack defaultMusic = MusicHandler.getDefaultTrack(context.getServer());
            msg.addField("default", "[" + defaultMusic.getInfo().title + "](" + defaultMusic.getInfo().uri + ")", false);
        }
        if (MusicHandler.isPlayingDefaultMusic(context.getServer())) {
            msg.sendMessage(context.getChannel());
            return;
        }
        msg.addField("current", "[" + current.getInfo().title + "](" + current.getInfo().uri + ")", false);
        int count = 1;
        for (AudioTrack track : tracks) {
            msg.addField(count + "th", "[" + track.getInfo().title + "](" + track.getInfo().uri + ")", false);
            count++;
        }
        msg.sendMessage(context.getChannel());
    }
}