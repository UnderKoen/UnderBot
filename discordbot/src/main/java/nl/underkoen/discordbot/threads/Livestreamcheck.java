package nl.underkoen.discordbot.threads;

import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.entities.DChannel;
import nl.underkoen.discordbot.utils.YoutubeUtil;

import java.util.concurrent.TimeUnit;

/**
 * Created by Under_Koen on 09-05-17.
 */
public class Livestreamcheck extends Thread {
    public boolean check = true;
    public DChannel channel;

    public static final String CHANNEL_ID = "UC8cgXXpepeB2CWfxdy7uGVg";

    private String current = "";
    private String last = "";

    @Override
    public void run() {
        try {
            while (check) {
                current = YoutubeUtil.getLivestream(CHANNEL_ID);
                if (!current.isEmpty() && DiscordBot.client.isLoggedIn()) {
                    if (!current.contentEquals(last))
                        channel.sendMessage("@everyone Tim is live op https://www.youtube.com/makertim/live");
                    last = current;
                }
                sleep(TimeUnit.MINUTES.toMillis(5));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopCheck() {
        check = false;
    }

    public void setChannel(DChannel channel) {
        this.channel = channel;
    }
}