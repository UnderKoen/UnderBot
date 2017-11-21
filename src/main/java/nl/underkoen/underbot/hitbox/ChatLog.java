package nl.underkoen.underbot.hitbox;

import java.sql.Timestamp;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface ChatLog extends Response {
    default String getMethod() {return "chatLog";}

    String getText();

    String getChannel();

    Timestamp getTime();

    String getPopover();
}
