package nl.underkoen.discordbot;

/**
 * Created by Under_Koen on 10-05-17.
 */
public enum Roles {
    EVERYONE(0), GAMEMAKER(1), MODJAM(2), UNDERBOT(3), NIGHTBOT(4), SUPPORTER(5), SUPER_SUPPORTER(6), SUPER_SUPPORTER_HITBOX(7), SUPER_SUPPORTER_TWITCH(8), MOD(9), YOUTUBER(10), DECLUB(11), MUTED(12), ADMIN(13);

    public int role;

    Roles(int role) {
        this.role = role;
    }
}