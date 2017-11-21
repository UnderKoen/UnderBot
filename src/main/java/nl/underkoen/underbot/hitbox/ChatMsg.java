package nl.underkoen.underbot.hitbox;

import java.awt.*;
import java.sql.Timestamp;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface ChatMsg extends Response {
    default String getMethod() {return "chatMsg";}

    String getChannel();

    UserInfo getUser();

    Color getNameColor();

    String getText();

    Timestamp getTime();

    RoleUtil.Role getRole();

    Boolean hasMedia();

    String getImage();
}