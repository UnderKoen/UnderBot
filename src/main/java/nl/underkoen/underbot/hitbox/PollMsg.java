package nl.underkoen.underbot.hitbox;

import com.google.gson.JsonObject;

import java.awt.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface PollMsg extends Response {
    default String getMethod() {return "pollMsg";}

    String getChannel();

    String getQuestion();

    List<JsonObject> getChoices();

    Timestamp getStartTime();

    Integer getClientID();

    PollStatus getPollStatus();

    Color getNameColor();

    Boolean isSubscriberOnly();

    Boolean isFollowerOnly();

    Integer getVotes();

    List<UserInfo> getVoters();
}

enum PollStatus {
    started, paused, ended;
}
