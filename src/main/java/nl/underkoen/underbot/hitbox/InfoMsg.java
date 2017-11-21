package nl.underkoen.underbot.hitbox;

import com.google.gson.JsonObject;

import java.sql.Timestamp;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface InfoMsg extends Response {
    default String getMethod() {return "infoMsg";}

    String getText();

    String getChannel();

    String getLang();

    UserInfo getSubscriber();

    JsonObject getVariables();

    Timestamp getTime();

    UserInfo getUser();

    Action getAction();

    String getType();
}

enum Action {
    kicked, ban, banip, unban, isAdmin, removeMod, directmsg;
}
