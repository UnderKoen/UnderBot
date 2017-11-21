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
import sx.blah.discord.handle.obj.IUser;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Under_Koen on 05/11/2017.
 */
public class LinkDiscord implements Listener {
    public LinkDiscord() throws Exception {
        File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        file = new File(file.getParent() + "/LinkedHitbox.json");
        if (!file.exists()) {
            file.createNewFile();
            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(getResource());
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        new SupporterCheck().start();
    }

    private static String getResource() {

        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream file = classLoader.getResourceAsStream("LinkedHitbox.json");

        try (Scanner scanner = new Scanner(file, "UTF-8")) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();

    }

    public static String getFile() {

        StringBuilder result = new StringBuilder("");

        try {
            File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            file = new File(file.getParent() + "/LinkedHitbox.json");

            try (Scanner scanner = new Scanner(file, "UTF-8")) {

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    result.append(line).append("\n");
                }

                scanner.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
        }

        return result.toString();
    }

    public static void addUser(String discordName, String discordId, String hitboxName, boolean isSubscriber) {
        JsonObject json = new JsonParser().parse(getFile()).getAsJsonObject();
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
            File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            file = new File(file.getParent() + "/LinkedHitbox.json");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json.toString());
            fileWriter.flush();
            fileWriter.close();
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
