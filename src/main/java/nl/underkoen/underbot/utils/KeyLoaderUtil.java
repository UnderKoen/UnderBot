package nl.underkoen.underbot.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by Under_Koen on 27/09/2017.
 */
public class KeyLoaderUtil {
    private JsonObject keys;

    public KeyLoaderUtil(JsonObject keys) {
        this.keys = keys;
    }

    public KeyLoaderUtil(String keys) {
        this.keys = new JsonParser().parse(keys).getAsJsonObject();
    }

    public String getDiscordKey() {
        return keys.get("discord_key").getAsString();
    }

    public String getYoutubeKey() {
        return keys.get("youtube_key").getAsString();
    }

    public String getHitboxName() {
        return keys.getAsJsonObject("hitbox").get("name").getAsString();
    }
    public String getHitboxPass() {
        return keys.getAsJsonObject("hitbox").get("pass").getAsString();
    }

    /**
     * consumerKey, consumerSecret, token, tokenSecret
     */
    public String[] getTwitterKeys() {
        return new String[]{
                keys.getAsJsonObject("twitter").get("consumerKey").getAsString(),
                keys.getAsJsonObject("twitter").get("consumerSecret").getAsString(),
                keys.getAsJsonObject("twitter").get("token").getAsString(),
                keys.getAsJsonObject("twitter").get("tokenSecret").getAsString()
        };
    }
}
