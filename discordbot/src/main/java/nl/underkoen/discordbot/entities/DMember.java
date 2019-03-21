package nl.underkoen.discordbot.entities;

import lombok.Getter;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import nl.underkoen.chatbot.models.Member;
import nl.underkoen.discordbot.utils.RoleUtil;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 20/11/2017.
 */
public class DMember implements Member<DServer, DUser> {
    private static List<DMember> members = new ArrayList<>();

    public static DMember getMember(net.dv8tion.jda.core.entities.Member member) {
        return getMember(member.getGuild(), member.getUser());
    }

    public static DMember getMember(Guild guild, User user) {
        return getMember(DServer.getServer(guild), DUser.getUser(user));
    }

    public static DMember getMember(DServer server, User user) {
        return getMember(server, DUser.getUser(user));
    }

    public static DMember getMember(Guild guild, DUser user) {
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

    private DServer server;
    private DUser user;

    @Getter
    private net.dv8tion.jda.core.entities.Member member;

    private DMember(DServer server, DUser user) {
        this.server = server;
        this.user = user;
        this.member = server.getGuild().getMember(user.getUser());
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
        return member.getJoinDate().toLocalDateTime();
    }

    public VoiceState getVoiceState() {
        return member.getVoiceState();
    }

    public String getGame() {
        return member.getGame().getName();
    }

    public OnlineStatus getOnlineStatus() {
        return member.getOnlineStatus();
    }

    public String getNickName() {
        return member.getNickname();
    }

    public String getEffectiveName() {
        return member.getEffectiveName();
    }

    public String getAsMention() {
        return member.getAsMention();
    }

    public List<Role> getRoles() {
        return member.getRoles();
    }

    public Color getColor() {
        return member.getColor();
    }

    public List<Permission> getPermissions(Channel channel) {
        return member.getPermissions(channel);
    }

    public List<Permission> getPermissions() {
        return member.getPermissions();
    }

    public boolean isOwner() {
        return member.isOwner();
    }
}
