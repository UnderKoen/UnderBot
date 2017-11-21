package nl.underkoen.underbot;

/**
 * Created by Under_Koen on 10-05-17.
 */
public enum Roles {
    EVERYONE(0), MUTED(1), UNDERBOT(2), NIGHTBOT(3), SUPPORTER(4), SUPER_SUPPORTER(5), SUPER_SUPPORTER_HITBOX(6), MOD(7), YOUTUBER(8), ADMIN(9);

    public int role;

    Roles(int role) {
        this.role = role;
    }
}