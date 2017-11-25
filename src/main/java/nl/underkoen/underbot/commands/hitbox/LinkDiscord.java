package nl.underkoen.underbot.commands.hitbox;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.hitbox.ChatMsg;
import nl.underkoen.underbot.hitbox.Listener;
import nl.underkoen.underbot.hitbox.Response;
import nl.underkoen.underbot.hitbox.UserInfo;
import nl.underkoen.underbot.threads.SupporterCheck;
import nl.underkoen.underbot.utils.FileUtil;
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
        new SupporterCheck().start();
    }

    public static void addUser(String discordName, String discordId, String hitboxName, boolean isSubscriber) {
        JsonObject json;
        try {
            json = new JsonParser().parse(FileUtil.getFile("LinkedHitbox.json")).getAsJsonObject();
        } catch (FileNotFoundException | URISyntaxException e) {
            e.printStackTrace();
            return;

        }
        for (JsonElement jsonE : json.getAsJsonArray("linked")) {
            if (jsonE.getAsJsonObject().get("hitboxName").getAsString().equalsIgnoreCase(hitboxName)) {
                json.getAsJsonArray("linked").remove(jsonE);
                break;
            }
        }
        JsonObject linked = new JsonObject();
        linked.addProperty("discordName", discordName);
        linked.addProperty("discordId", discordId);
        linked.addProperty("hitboxName", hitboxName);
        linked.addProperty("isSubscriber", isSubscriber);
        json.getAsJsonArray("linked").add(linked);

        try {
            FileUtil.updateFile("LinkedHitbox.json", json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                addUser(discordName, discordId, hitboxName, isSubscriber);
                Main.hitboxUtil.sendMessage("@" + user.getName() + ", Linked your hitbox account to your discord account", Color.RED);
            } catch (Exception ex) {
                Main.hitboxUtil.sendMessage("@" + user.getName() + ", the correct way is !discordlink @(DiscordName)#(Discord Tag)", Color.RED);
            }
        }
    }
}
