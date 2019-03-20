package nl.underkoen.twitchbot;

import nl.underkoen.twitchbot.chatbot.TMember;

/**
 * Created by Under_Koen on 03/09/2018.
 */
public enum Roles {
    NONE(0), TURBO(1), SUBSCRIBER(1), MOD(2), OWNER(3);

    public int role;

    Roles(int role) {
        this.role = role;
    }

    public static Roles getRole(TMember member) {
        if (member.isOwner()) {
            return Roles.OWNER;
        } else if (member.isMod()) {
            return Roles.MOD;
        } else if (member.isSub()) {
            return Roles.SUBSCRIBER;
        } else if (member.isTurbo()) {
            return Roles.TURBO;
        } else {
            return Roles.NONE;
        }
    }
}
