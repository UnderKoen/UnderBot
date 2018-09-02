package nl.underkoen.discordbot.utils;

import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.entities.DServer;
import sx.blah.discord.handle.obj.IRole;

/**
 * Created by Under_Koen on 06-05-17.
 */
public class RoleUtil {
    public static IRole getHighestRole(DMember user) {
        IRole highest = user.getServer().getGuild().getEveryoneRole();
        for (IRole role : user.getRoles()) {
            if (highest.getPosition() >= role.getPosition()) continue;
            highest = role;
        }
        return highest;
    }

    public static IRole getHighestRole(DServer server) {
        IRole highest = server.getGuild().getEveryoneRole();
        for (IRole role : server.getGuild().getRoles()) {
            if (highest.getPosition() >= role.getPosition()) continue;
            highest = role;
        }
        return highest;
    }

    public static IRole getRole(DServer server, int position) {
        IRole role = null;
        for (IRole rol : server.getGuild().getRoles()) {
            if (position != rol.getPosition()) continue;
            role = rol;
        }
        if (role != null) return role;
        int lowest = 0;
        for (IRole rol : server.getGuild().getRoles()) {
            if (position < rol.getPosition()) continue;
            if (!(lowest < rol.getPosition() || lowest == 0)) continue;
            lowest = rol.getPosition();
            role = rol;
        }
        return role;
    }
}
