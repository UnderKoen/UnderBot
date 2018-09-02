package nl.underkoen.discordbot.utils.Messages;

import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.entities.DChannel;
import nl.underkoen.discordbot.entities.DMember;
import sx.blah.discord.handle.obj.IEmbed.IEmbedField;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Under_Koen on 19-04-17.
 */
public abstract class UnderMessage {
    public abstract Color getColor();

    public abstract DMember getAuthor();

    public List<IEmbedField> getFields() {
        return new ArrayList<>();
    }

    public String getDescription() {
        return null;
    }

    public String getTitle() {
        return null;
    }

    public String getUrl() {
        return null;
    }

    public void lastCheck(EmbedBuilder msg, DChannel channel) {
    }

    public void sendMessage(DChannel channel) {
        EmbedBuilder msg = new EmbedBuilder();

        Color color = getColor();
        if (color != null) {
            msg.withColor(color);
        }

        DMember author = getAuthor();
        if (author != null) {
            msg.withFooterText(author.getEffectiveName());
            msg.withFooterIcon(author.getUser().getUser().getAvatarURL());
        }

        List<IEmbedField> fields = getFields();
        if (fields != null) {
            for (IEmbedField field : fields) {
                msg.appendField(field);
            }
        }

        String desc = getDescription();
        if (desc != null) {
            msg.withDescription(desc);
        }

        String title = getTitle();
        String url = getUrl();
        if (title != null) {
            msg.withTitle(title);
            msg.withUrl(url);
        }

        lastCheck(msg, channel);

        IMessage ms = channel.getChannel().sendMessage(msg.build());

        DiscordBot.timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        ms.delete();
                    }
                },
                TimeUnit.MINUTES.toMillis(5)
        );
    }
}
