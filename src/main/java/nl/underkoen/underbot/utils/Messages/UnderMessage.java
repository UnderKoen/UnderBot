package nl.underkoen.underbot.utils.Messages;

import nl.underkoen.underbot.entities.Member;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IEmbed.IEmbedField;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Under_Koen on 19-04-17.
 */
public interface UnderMessage {

    default Color getColor() {
        return null;
    }

    default Member getAuthor() {
        return null;
    }

    default List<IEmbedField> getFields() {
        return new ArrayList<IEmbedField>();
    }

    default String getDescription() {
        return null;
    }

    default String getTitle() {
        return null;
    }

    default String getUrl() {
        return null;
    }

    default void sendMessage(IChannel channel) {
        EmbedBuilder msg = new EmbedBuilder();

        Color color = getColor();
        if (color != null) {
            msg.withColor(color);
        }

        Member author = getAuthor();
        if (author != null) {
            msg.withFooterText(author.getEffectiveName());
            msg.withFooterIcon(author.getUser().getAvatarURL());
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

        IMessage ms = channel.sendMessage(msg.build());

        new Timer().schedule(
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
