package nl.underkoen.underbot.hitbox;

import java.util.List;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface UserList extends Response {
    default String getMethod() {return "userList";}

    String getChannel();

    Integer getGuests();

    List<UserInfo> getAdmins();

    List<UserInfo> getMods();

    List<UserInfo> getNormalUsers();

    List<UserInfo> getFollowers();

    List<UserInfo> getSubscribers();

    List<UserInfo> getStaff();

    List<UserInfo> getCommunity();

    List<UserInfo> getAllUsers();
}
