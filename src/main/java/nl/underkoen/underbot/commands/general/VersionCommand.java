package nl.underkoen.underbot.commands.general;

import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.utils.Messages.TextMessage;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Under_Koen on 09-05-17.
 */
public class VersionCommand implements Command {
    private String command = "versions";
    private String usage = "/versions";
    private String description = "Returns all the versions of the bot.";
    private ArrayList<String> versions = new ArrayList<String>();

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
        File folder = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        folder = new File(folder.getParent() + "/Changelogs");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (!listOfFiles[i].isFile()) continue;
            versions.add(listOfFiles[i].getName().replace("Changelog_", "").replace(".json", ""));
        }
    }

    @Override
    public void run(CommandContext context) {
        StringBuilder str = new StringBuilder();
        for (String version : versions) {
            str.append("- ").append(version).append("\n");
        }
        new TextMessage().setMention(context.getMember()).addText("All version for /changelog [version]")
                .addField("Versions", str.toString(), false).sendMessage(context.getChannel());
    }
}