package nl.underkoen.underbot.hitbox;

import java.sql.Timestamp;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface UserInfo extends Response {
    default String getMethod() {return "userInfo";}

    String getChannel();

    String getName();

    Timestamp getTime();

    RoleUtil.Role getRole();

    Boolean isFollower();

    Boolean isSubscriber();

    Boolean isOwner();

    Boolean isStaff();

    Boolean isCommunity();
}
