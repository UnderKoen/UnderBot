package nl.underkoen.underbot.music.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.music.MusicHandler;
import nl.underkoen.underbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 10-05-17.
 */
public class QueueCommand implements Command {
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
    public void setup() throws Exception {
    }

    @Override
    public void run(CommandContext context) {
        AudioTrack[] tracks = MusicHandler.getQueue(context.getGuild());
        AudioTrack current = MusicHandler.getCurrentTrack(context.getGuild());
        if (tracks.length == 0 && current == null) {
            new TextMessage().setMention(context.getMember()).addText("The queue is empty").sendMessage(context.getChannel());
            return;
        }
        TextMessage msg = new TextMessage().setMention(context.getMember()).addText("The queue is:");
        if (MusicHandler.hasDefaultMusic(context.getGuild())) {
            AudioTrack defaultMusic = MusicHandler.getDefaultTrack(context.getGuild());
            msg.addField("default", "[" + defaultMusic.getInfo().title + "](" + defaultMusic.getInfo().uri + ")", false);
        }
        if (MusicHandler.isPlayingDefaultMusic(context.getGuild())) {
            msg.sendMessage(context.getChannel());
            return;
        }
        msg.addField("current", "[" + current.getInfo().title + "](" + current.getInfo().uri + ")", false);
        int count = 1;
        for (AudioTrack track: tracks) {
            msg.addField(count + "th", "[" + track.getInfo().title + "](" + track.getInfo().uri + ")", false);
            count++;
        }
        msg.sendMessage(context.getChannel());
    }
}