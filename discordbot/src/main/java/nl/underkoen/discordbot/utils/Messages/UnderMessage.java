package nl.underkoen.discordbot.utils.Messages;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.entities.DChannel;
import nl.underkoen.discordbot.entities.DMember;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.core.entities.MessageEmbed.Field;

/**
 * Created by Under_Koen on 19-04-17.
 */
public abstract class UnderMessage {
    public abstract Color getColor();

    public abstract DMember getAuthor();

    public List<Field> getFields() {
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
            msg.setColor(color);
        }

        DMember author = getAuthor();
        if (author != null) {
            msg.setFooter(author.getEffectiveName(), author.getUser().getUser().getAvatarUrl());
        }

        List<Field> fields = getFields();
        if (fields != null) {
            for (Field field : fields) {
                msg.addField(field);
            }
        }

        String desc = getDescription();
        if (desc != null) {
            msg.setDescription(desc);
        }

        String title = getTitle();
        String url = getUrl();
        if (title != null) {
            msg.setTitle(title, url);
        }

        lastCheck(msg, channel);

        Message ms = channel.getChannel().sendMessage(msg.build()).complete();
        DiscordBot.timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        ms.delete().complete();
                    }
                },
                TimeUnit.MINUTES.toMillis(5)
        );
    }
}
