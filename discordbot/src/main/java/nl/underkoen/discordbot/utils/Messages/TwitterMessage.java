package nl.underkoen.discordbot.utils.Messages;

import nl.underkoen.discordbot.utils.ColorUtil;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Under_Koen on 06/09/2017.
 */
public class TwitterMessage implements UnderMessage {
    private Color color = ColorUtil.hexToColor("#2a6886");

    private Status status;

    public TwitterMessage setStatus(Status status) {
        this.status = status;
        return this;
    }

    @Override
    public void sendMessage(IChannel channel) {

        //System.out.println();

        EmbedBuilder msg = new EmbedBuilder();

        if (color != null) {
            msg.withColor(color);
        }
        msg.withAuthorName(status.getUser().getName() + " (@" + status.getUser().getScreenName() + ")");
        msg.withAuthorIcon(status.getUser().getOriginalProfileImageURL());
        msg.withAuthorUrl("https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId());

        String text = status.getText();

        for (URLEntity url : status.getURLEntities()) {
            msg.withImage(url.getExpandedURL());
            text = text.replace(url.getURL(), url.getExpandedURL());
        }

        if (status.isRetweet()) {
            for (URLEntity url : status.getRetweetedStatus().getURLEntities()) {
                msg.withImage(url.getExpandedURL());
                text = text.replace(url.getURL(), url.getExpandedURL());
            }
        }

        for (MediaEntity url : status.getMediaEntities()) {
            msg.withImage(url.getMediaURLHttps());
            text = text.replace(url.getURL(), url.getExpandedURL());
        }

        if (status.isRetweet()) {
            for (MediaEntity url : status.getRetweetedStatus().getMediaEntities()) {
                msg.withImage(url.getMediaURLHttps());
                text = text.replace(url.getURL(), url.getExpandedURL());
            }
        }

        msg.withDescription(text);

        msg.withFooterText("Twitter");
        msg.withFooterIcon("https://images-ext-1.discordapp.net/external/bXJWV2Y_F3XSra_kEqIYXAAsI3m1meckfLhYuWzxIfI/https/abs.twimg.com/icons/apple-touch-icon-192x192.png");

        IMessage ms = channel.sendMessage(msg.build());

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        ms.delete();
                    }
                },
                TimeUnit.DAYS.toMillis(1)
        );
    }
}
