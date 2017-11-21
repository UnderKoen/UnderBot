package nl.underkoen.underbot.hitbox;

/**
 * Created by Under_Koen on 04/11/2017.
 */
public class RoleUtil {
    public enum Role {
        BANNED("banned"), GUEST("guest"), USER("anon"), MOD("user"), ADMIN("admin");

        private String roleName;

        Role(String roleName) {
            this.roleName = roleName;
        }
    }

    public static Role getRoleName(String roleName) {
        switch (roleName) {
            case "banned":
                return Role.BANNED;
            case "guest":
                return Role.GUEST;
            case "anon":
                return Role.USER;
            case "user":
                return Role.MOD;
            case "admin":
                return Role.ADMIN;
            default:
                return null;
        }
    }
}
