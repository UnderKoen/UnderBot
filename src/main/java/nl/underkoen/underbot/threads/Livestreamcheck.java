package nl.underkoen.underbot.threads;

import nl.underkoen.underbot.utils.YoutubeUtil;
import sx.blah.discord.handle.obj.IChannel;

/**
 * Created by Under_Koen on 09-05-17.
 */
public class Livestreamcheck extends Thread {
    public boolean check = true;
    public IChannel channel;

    public static String channelId = "UC8cgXXpepeB2CWfxdy7uGVg";

    public String Current = "";
    public String Last = "";

    @Override
    public void run() {
        try {
            while (check) {
                Current = YoutubeUtil.getLivestream(channelId);
                if (!Current.contentEquals(Last)) {
                    if (!Current.isEmpty()) channel.sendMessage("@everyone Tim is live op https://www.youtube.com/makertim/live");
                }
                Last = Current;
                this.sleep(5 * 60 * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopCheck() {
        check = false;
    }

    public void setChannel(IChannel channel) {
        this.channel = channel;
    }
}