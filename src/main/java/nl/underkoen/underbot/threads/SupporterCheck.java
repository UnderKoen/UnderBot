package nl.underkoen.underbot.threads;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.Roles;
import nl.underkoen.underbot.commands.hitbox.LinkDiscord;
import nl.underkoen.underbot.entities.Member;
import nl.underkoen.underbot.entities.impl.MemberImpl;
import nl.underkoen.underbot.hitbox.UserInfo;
import nl.underkoen.underbot.utils.RoleUtil;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Under_Koen on 05/11/2017.
 */
public class SupporterCheck extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                JsonObject json = new JsonParser().parse(LinkDiscord.getFile()).getAsJsonObject();
                for (JsonElement jsonE : json.getAsJsonArray("linked")) {
                    JsonObject linked = jsonE.getAsJsonObject();
                    UserInfo userInfo = Main.hitboxUtil.getUserInfo(linked.get("hitboxName").getAsString());
                    String discordName = linked.get("discordName").getAsString();
                    String discordId = linked.get("discordId").getAsString();
                    String hitboxName = userInfo.getName();
                    boolean isSubscriberbefore = linked.get("isSubscriber").getAsBoolean();
                    boolean isSubscriber = userInfo.isSubscriber();
                    LinkDiscord.addUser(discordName, discordId, hitboxName, isSubscriber);
                    if (isSubscriber) {
                        IGuild guild = Main.client.getGuilds().get(0);
                        IUser user = getUser(discordName, discordId);
                        if (user != null) {
                            Member member = new MemberImpl(guild, user);
                            List<IRole> roles = member.getRoles();
                            IRole role = RoleUtil.getRole(guild, Roles.SUPER_SUPPORTER_HITBOX.role);
                            if (!roles.contains(role)) {
                                roles.add(role);
                                member.getGuild().editUserRoles(member.getUser(), (IRole[]) roles.toArray());
                            }
                        }
                    } else if (isSubscriber != isSubscriberbefore) {
                        IGuild guild = Main.client.getGuilds().get(0);
                        IUser user = getUser(discordName, discordId);
                        if (user != null) {
                            Member member = new MemberImpl(guild, user);
                            List<IRole> roles = member.getRoles();
                            IRole role = RoleUtil.getRole(guild, Roles.SUPER_SUPPORTER_HITBOX.role);
                            if (roles.contains(role)) {
                                roles.remove(role);
                                member.getGuild().editUserRoles(member.getUser(), (IRole[]) roles.toArray());
                            }
                        }
                    }
                }
                sleep(TimeUnit.MINUTES.toMillis(30));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private IUser getUser(String name, String id) {
        IUser hit = null;
        for (IUser user : Main.client.getUsersByName(name, true)) {
            if (user.getDiscriminator().equalsIgnoreCase(id)) {
                hit = user;
                break;
            }
        }
        return hit;
    }
}
