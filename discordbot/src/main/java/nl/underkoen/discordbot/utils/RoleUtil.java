package nl.underkoen.discordbot.utils;

import net.dv8tion.jda.core.entities.Role;
import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.entities.DServer;

/**
 * Created by Under_Koen on 06-05-17.
 */
public class RoleUtil {
    public static Role getHighestRole(DMember user) {
        Role highest = user.getServer().getGuild().getPublicRole();
        for (Role role : user.getRoles()) {
            if (highest.getPosition() >= role.getPosition()) continue;
            highest = role;
        }
        return highest;
    }

    public static Role getHighestRole(DServer server) {
        Role highest = server.getGuild().getPublicRole();
        for (Role role : server.getGuild().getRoles()) {
            if (highest.getPosition() >= role.getPosition()) continue;
            highest = role;
        }
        return highest;
    }

    public static Role getRole(DServer server, int position) {
        Role role = null;
        for (Role rol : server.getGuild().getRoles()) {
            if (position != rol.getPosition()) continue;
            role = rol;
        }
        if (role != null) return role;
        int lowest = 0;
        for (Role rol : server.getGuild().getRoles()) {
            if (position < rol.getPosition()) continue;
            if (!(lowest < rol.getPosition() || lowest == 0)) continue;
            lowest = rol.getPosition();
            role = rol;
        }
        return role;
    }
}
