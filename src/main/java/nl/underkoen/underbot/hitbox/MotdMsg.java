package nl.underkoen.underbot.hitbox;

import java.awt.*;
import java.sql.Timestamp;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface MotdMsg extends Response {
    default String getMethod() {return "motdMsg";}

    String getChannel();

    UserInfo getUser();

    Color getNameColor();

    String getText();

    Timestamp getTime();

    String getImage();
}
