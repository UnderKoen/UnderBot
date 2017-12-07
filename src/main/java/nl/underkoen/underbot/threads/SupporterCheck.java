package nl.underkoen.underbot.threads;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.underkoen.underbot.commands.hitbox.LinkDiscord;
import nl.underkoen.underbot.utils.FileUtil;

import java.util.concurrent.TimeUnit;

/**
 * Created by Under_Koen on 05/11/2017.
 */
public class SupporterCheck extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                JsonObject json = new JsonParser().parse(FileUtil.getFile("LinkedHitbox.json")).getAsJsonObject();
                for (JsonElement jsonE : json.getAsJsonArray("linked")) {
                    JsonObject linked = jsonE.getAsJsonObject();
                    LinkDiscord.giveSupporter(linked.get("hitboxName").getAsString());
                }
                Thread.sleep(TimeUnit.MINUTES.toMillis(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
