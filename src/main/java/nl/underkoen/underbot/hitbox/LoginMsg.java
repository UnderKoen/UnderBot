package nl.underkoen.underbot.hitbox;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public interface LoginMsg extends Response {
    default String getMethod() {return "loginMsg";}

    String getChannel();

    UserInfo getUser();

    RoleUtil.Role getRole();
}
