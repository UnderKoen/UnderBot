package nl.underkoen.discordbot.entities;

import nl.underkoen.chatbot.models.Member;
import nl.underkoen.discordbot.utils.RoleUtil;
import sx.blah.discord.handle.obj.*;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Under_Koen on 20/11/2017.
 */
public class DMember implements Member<DServer, DUser> {
    private static List<DMember> members = new ArrayList<>();

    public static DMember getMember(IGuild guild, IUser user) {
        return getMember(DServer.getServer(guild), DUser.getUser(user));
    }

    public static DMember getMember(DServer server, IUser user) {
        return getMember(server, DUser.getUser(user));
    }

    public static DMember getMember(IGuild guild, DUser user) {
        return getMember(DServer.getServer(guild), user);
    }

    public static DMember getMember(DServer server, DUser user) {
        return members.stream()
                .filter(member -> member.getServer().equals(server))
                .filter(member -> member.getUser().equals(user))
                .findFirst()
                .orElseGet(() -> {
                    DMember member = new DMember(server, user);
                    members.add(member);
                    return member;
                });
    }

    DServer server;
    DUser user;

    private DMember(DServer server, DUser user) {
        this.server = server;
        this.user = user;
    }

    @Override
    public DUser getUser() {
        return user;
    }

    @Override
    public int getHighestRank() {
        return RoleUtil.getHighestRole(this).getPosition();
    }

    @Override
    public DServer getServer() {
        return server;
    }

    public LocalDateTime getJoinDate() {
        return LocalDateTime.ofInstant(server.getGuild().getJoinTimeForUser(user.getUser()), ZoneOffset.UTC);
    }

    public IVoiceState getVoiceState() {
        return user.getUser().getVoiceStateForGuild(server.getGuild());
    }

    public Optional<String> getGame() {
        return user.getUser().getPresence().getText();
    }

    public StatusType getOnlineStatus() {
        return user.getUser().getPresence().getStatus();
    }

    public String getNickName() {
        return user.getUser().getNicknameForGuild(server.getGuild());
    }

    public String getEffectiveName() {
        return user.getUser().getDisplayName(server.getGuild());
    }

    public String getAsMention() {
        return user.getUser().mention();
    }

    public List<IRole> getRoles() {
        return user.getUser().getRolesForGuild(server.getGuild());
    }

    public Color getColor() {
        return user.getUser().getColorForGuild(server.getGuild());
    }

    public Set<Permissions> getPermissions(IChannel channel) {
        return channel.getModifiedPermissions(user.getUser());
    }

    public Set<Permissions> getPermissions() {
        return user.getUser().getPermissionsForGuild(server.getGuild());
    }

    public boolean isOwner() {
        return server.getGuild().getOwner().equals(user.getUser());
    }
}
