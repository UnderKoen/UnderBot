package nl.underkoen.underbot.commands.supporter;

import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.Roles;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.utils.Messages.TextMessage;
import sx.blah.discord.handle.obj.IUser;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Under_Koen on 03-05-17.
 */
public class AandachtCommand implements Command {
    private String command = "aandacht";
    private String usage = "/aandacht";
    private String description = "Sends a line from AANDACHT - Kud";
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
        file = new File(file.getParent() + "/Aandacht.txt");
        if (!file.exists()) {
            file.createNewFile();
            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(getAandachtResource());
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        aandacht = getAandacht();
    }

    private String getAandachtResource() {

        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream file = classLoader.getResourceAsStream("Aandacht.txt");

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

    private ArrayList<String> getAandacht() {

        ArrayList<String> result = new ArrayList<String>();

        try {
            File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            file = new File(file.getParent() + "/Aandacht.txt");

            try (Scanner scanner = new Scanner(file, "UTF-8")) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    result.add(line);
                }
                scanner.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {}

        return result;
    }

    private ArrayList<String> aandacht;
    private int current = 0;
    private IUser last;

    @Override
    public void run(CommandContext context) throws Exception {
        if (context.getUser() != last) {
            new TextMessage().addText(aandacht.get(current)).setMention(context.getMember()).sendMessage(context.getChannel());
            last = context.getUser();
            if (current == aandacht.size()-1) {
                current = 0;
            } else {
                current = current+1;
            }
        }
    }
}
