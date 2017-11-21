package nl.underkoen.underbot.hitbox;

import java.sql.Timestamp;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface SlowMsg extends Response {
    default String getMethod() {return "slowMsg";}

    String getText();

    String getChannel();

    Timestamp getTime();

    String getAction();

    Integer getSlowTime();
}
