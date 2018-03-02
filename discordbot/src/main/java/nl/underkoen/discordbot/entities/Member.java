package nl.underkoen.discordbot.entities;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Created by Under_Koen on 20/11/2017.
 */
public interface Member {
    IUser getUser();

    IGuild getGuild();

    IDiscordClient getClient();

    LocalDateTime getJoinDate();

    IVoiceState getVoiceState();

    Optional<String> getGame();

    StatusType getOnlineStatus();

    String getNickName();

    String getEffectiveName();

    String getAsMention();

    List<IRole> getRoles();

    Color getColor();

    EnumSet<Permissions> getPermissions(IChannel channel);

    EnumSet<Permissions> getPermissions();

    boolean isOwner();
}
