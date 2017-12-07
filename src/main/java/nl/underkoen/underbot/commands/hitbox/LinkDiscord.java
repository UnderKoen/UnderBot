package nl.underkoen.underbot.commands.hitbox;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.Roles;
import nl.underkoen.underbot.hitbox.ChatMsg;
import nl.underkoen.underbot.hitbox.Listener;
import nl.underkoen.underbot.hitbox.Response;
import nl.underkoen.underbot.hitbox.UserInfo;
import nl.underkoen.underbot.threads.SupporterCheck;
import nl.underkoen.underbot.utils.FileUtil;
import nl.underkoen.underbot.utils.RoleUtil;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

import java.awt.*;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Under_Koen on 05/11/2017.
 */
public class LinkDiscord implements Listener {
    public LinkDiscord() throws Exception {
        FileUtil.makeDuplicate("LinkedHitbox.json");
    }

    public static void addUser(String discordName, String discordId, String hitboxName) {
        JsonObject json;
        JsonObject newJson = new JsonObject();
        try {
            json = new JsonParser().parse(FileUtil.getFile("LinkedHitbox.json")).getAsJsonObject();
        } catch (FileNotFoundException | URISyntaxException e) {
            e.printStackTrace();
            return;

        }
        JsonArray jsonArray = new JsonArray();
        for (JsonElement jsonE : json.getAsJsonArray("linked")) {
            if (!jsonE.getAsJsonObject().get("hitboxName").getAsString().equalsIgnoreCase(hitboxName)) {
                jsonArray.add(jsonE);
            }
        }
        JsonObject linked = new JsonObject();
        linked.addProperty("discordName", discordName);
        linked.addProperty("discordId", discordId);
        linked.addProperty("hitboxName", hitboxName);
        jsonArray.add(linked);
        newJson.add("linked", jsonArray);

        try {
            FileUtil.updateFile("LinkedHitbox.json", newJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static IUser getUser(String hitboxName) {
        try {
            String jsonString = FileUtil.getFile("LinkedHitbox.json");
            JsonParser parser = new JsonParser();
            JsonElement jsonElement = parser.parse(jsonString);
            JsonObject json = jsonElement.getAsJsonObject();
            JsonArray users = json.getAsJsonArray("linked");
            for (JsonElement userEl : users) {
                JsonObject user = userEl.getAsJsonObject();
                if (user.get("hitboxName").getAsString().equalsIgnoreCase(hitboxName)) {
                    return getUser(user.get("discordName").getAsString(), user.get("discordId").getAsString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static IUser getUser(String name, String id) {
        IUser hit = null;
        for (IUser user : Main.client.getUsersByName(name, true)) {
            if (user.getDiscriminator().equalsIgnoreCase(id)) {
                hit = user;
                break;
            }
        }
        return hit;
    }

    public static void giveSupporter(String hitboxName) {
        try {
            IUser user = getUser(hitboxName);
            IGuild guild = Main.client.getGuilds().get(0);
            IRole role = RoleUtil.getRole(guild, Roles.SUPER_SUPPORTER_HITBOX.role);
            if (Main.hitboxUtil.isSubscriber(hitboxName)) {
                if (!user.hasRole(role)) {
                    user.addRole(role);
                }
            } else {
                if (user.hasRole(role)) {
                    user.removeRole(role);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setup() {
        new SupporterCheck().start();
    }

    @Override
    public void onEvent(Response response) {
        if (response instanceof ChatMsg) {
            ChatMsg chatMsg = (ChatMsg) response;
            String text = chatMsg.getText();
            UserInfo user = chatMsg.getUser();
            if (!text.startsWith("!linkdiscord")) return;
            text = text.replace("!linkdiscord", "");
            try {
                Pattern pattern = Pattern.compile(" @(.*)#([0-9][0-9][0-9][0-9])");
                Matcher matcher = pattern.matcher(text);
                matcher.find();
                String discordName = matcher.group(1);
                String discordId = matcher.group(2);

                IUser hit = null;
                for (IUser userD: Main.client.getUsersByName(discordName, true)) {
                    if (userD.getDiscriminator().equals(discordId)) hit = userD;
                }
                if (hit == null) {
                    Main.hitboxUtil.sendMessage("@" + user.getName() + ", we couldn't find you on discord. is your name and tag correct?", Color.RED);
                    return;
                }

                String hitboxName = user.getName();
                boolean isSubscriber = user.isSubscriber();

                addUser(discordName, discordId, hitboxName);
                if (isSubscriber) {
                    giveSupporter(hitboxName);
                }
                Main.hitboxUtil.sendMessage("@" + user.getName() + ", Linked your hitbox account to your discord account", Color.RED);
            } catch (Exception ex) {
                Main.hitboxUtil.sendMessage("@" + user.getName() + ", the correct way is !discordlink @(DiscordName)#(Discord Tag)", Color.RED);
            }
        }
    }
}
