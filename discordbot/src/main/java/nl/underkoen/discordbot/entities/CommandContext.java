package nl.underkoen.discordbot.entities;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Under_Koen on 19-04-17.
 */
public interface CommandContext {
    String getPrefix();

    String getCommand();

    String[] getArgs();

    String[] getRawArgs();

    IUser getUser();

    IChannel getChannel();

    IGuild getGuild();

    Member getMember();

    IMessage getMessage();
}
