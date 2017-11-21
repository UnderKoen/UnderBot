package nl.underkoen.underbot.hitbox;

import java.awt.*;
import java.sql.Timestamp;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface DirectMsg extends Response {
    default String getMethod() {return "directMsg";}

    UserInfo getFrom();

    Color getNameColor();

    String getText();

    Timestamp getTime();

    String getChannel();

    String getType();
}
