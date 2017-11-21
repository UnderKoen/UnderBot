package nl.underkoen.underbot.hitbox;

import java.util.List;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface BanList extends Response {
    default String getMethod() {return "banList";}

    String getChannel();

    List<UserInfo> getBans();
}
