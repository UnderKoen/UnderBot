package nl.underkoen.underbot.hitbox;

import com.google.gson.JsonObject;

import java.sql.Timestamp;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface ServerMsg extends Response {
    default String getMethod() {return "serverMsg";}

    String getChannel();

    JsonObject getText();

    String getType();

    Timestamp getTime();

    String getMode();
}
