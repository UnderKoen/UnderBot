package nl.underkoen.underbot.commands.supporter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.Roles;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.utils.Messages.ErrorMessage;
import nl.underkoen.underbot.utils.Messages.TextMessage;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by Under_Koen on 02-05-17.
 */
public class EmoteCommand implements Command {
    private String command = "emote";
    private String usage = "/emote [text...]";
    private String description = "Sends your message in emote style";
    private int minimumRole = Roles.SUPPORTER.role;

    @Override
    public int getMinimumRole() {
        return minimumRole;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setup() throws Exception {
        File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        file = new File(file.getParent() + "/Emote.json");
        if (file.exists()) return;
        file.createNewFile();
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(getEmotesResource());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getEmotesResource() {

        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream file = classLoader.getResourceAsStream("Emote.json");

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

    private String getEmotes() {

        StringBuilder result = new StringBuilder("");

        try {
            File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            file = new File(file.getParent() + "/Emote.json");

            try (Scanner scanner = new Scanner(file, "UTF-8")) {

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    result.append(line).append("\n");
                }

                scanner.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {}

        return result.toString();
    }

    @Override
    public void run(CommandContext context) throws Exception {
        if (context.getArgs().length == 0) {
            new ErrorMessage(context.getMember(), "No text to change to emote text")
                    .sendMessage(context.getChannel());
            return;
        }
        String args = "";
        for (String arg: context.getArgs()) {
            args = args + arg + " ";
        }
        args = args.trim().toLowerCase();
        String text = "";
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(getEmotes()).getAsJsonObject();
        TextMessage msg = new TextMessage().setMention(context.getMember());
        for (char Char: args.toCharArray()) {
            String add = (o.has(Char + "")) ? o.get(Char + "").getAsString() : o.get("none").getAsString();
            text = text + add + " ";
        }
        if (text.length() < 2048) {
            msg.addText(text).sendMessage(context.getChannel());
        } else {
            new TextMessage().setMention(context.getMember())
                    .addText("The amount of char's in emote form is over 2048 and can't be post sorry.")
                    .sendMessage(context.getChannel());
        }
    }
}
