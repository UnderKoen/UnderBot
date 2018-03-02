package nl.underkoen.discordbot.entities.impl;

import nl.underkoen.discordbot.entities.Member;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.*;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

/**
 * Created by Under_Koen on 21/11/2017.
 */
public class MemberImpl implements Member {
    IGuild guild;
    IUser user;


    public MemberImpl(IGuild guild, IUser user) {
        this.guild = guild;
        this.user = user;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public IGuild getGuild() {
        return guild;
    }

    @Override
    public IDiscordClient getClient() {
        return user.getClient();
    }

    @Override
    public LocalDateTime getJoinDate() {
        return guild.getJoinTimeForUser(user);
    }

    @Override
    public IVoiceState getVoiceState() {
        return user.getVoiceStateForGuild(guild);
    }

    @Override
    public Optional<String> getGame() {
        return user.getPresence().getPlayingText();
    }

    @Override
    public StatusType getOnlineStatus() {
        return user.getPresence().getStatus();
    }

    @Override
    public String getNickName() {
        return user.getNicknameForGuild(guild);
    }

    @Override
    public String getEffectiveName() {
        return user.getDisplayName(guild);
    }

    @Override
    public String getAsMention() {
        return user.mention();
    }

    @Override
    public List<IRole> getRoles() {
        return user.getRolesForGuild(guild);
    }

    @Override
    public Color getColor() {
        return user.getColorForGuild(guild);
    }

    @Override
    public EnumSet<Permissions> getPermissions(IChannel channel) {
        return channel.getModifiedPermissions(user);
    }

    @Override
    public EnumSet<Permissions> getPermissions() {
        return user.getPermissionsForGuild(guild);
    }

    @Override
    public boolean isOwner() {
        return guild.getOwner().equals(user);
    }
}
