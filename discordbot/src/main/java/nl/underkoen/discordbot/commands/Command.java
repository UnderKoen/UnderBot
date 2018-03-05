package nl.underkoen.discordbot.commands;

import nl.underkoen.discordbot.Roles;
import nl.underkoen.discordbot.entities.CommandContext;

/**
 * Created by Under_Koen on 19-04-17.
 */
public interface Command {
    String getCommand();

    String getUsage();

    String getDescription();

    default String[] getAliases() {
        return new String[]{};
    }

    default int getMinimumRole() {
        return Roles.EVERYONE.role;
    }

    default void setup() throws Exception {
    }

    default void stop() throws Exception {
    }

    void run(CommandContext context) throws Exception;
}
