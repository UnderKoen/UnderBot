package nl.underkoen.underbot.hitbox;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface MediaLog extends Response {
    default String getMethod() {return "mediaLog";}

    String getChannel();

    String getType();

    List<JsonObject> getData();
}
