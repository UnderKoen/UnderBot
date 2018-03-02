package nl.underkoen.discordbot.utils;

import nl.underkoen.discordbot.entities.Member;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;

/**
 * Created by Under_Koen on 06-05-17.
 */
public class RoleUtil {
    public static IRole getHighestRole(Member user) {
        IRole highest = user.getGuild().getEveryoneRole();
        for (IRole role : user.getRoles()) {
            if (highest.getPosition() >= role.getPosition()) continue;
            highest = role;
        }
        return highest;
    }

    public static IRole getHighestRole(IGuild guild) {
        IRole highest = guild.getEveryoneRole();
        for (IRole role : guild.getRoles()) {
            if (highest.getPosition() >= role.getPosition()) continue;
            highest = role;
        }
        return highest;
    }

    public static IRole getRole(IGuild guild, int position) {
        IRole role = null;
        for (IRole rol : guild.getRoles()) {
            if (position != rol.getPosition()) continue;
            role = rol;
        }
        if (role != null) return role;
        int lowest = 0;
        for (IRole rol : guild.getRoles()) {
            if (position < rol.getPosition()) continue;
            if (!(lowest < rol.getPosition() || lowest == 0)) continue;
            lowest = rol.getPosition();
            role = rol;
        }
        return role;
    }
}
