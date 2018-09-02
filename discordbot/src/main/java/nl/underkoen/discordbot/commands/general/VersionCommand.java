package nl.underkoen.discordbot.commands.general;

import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 09-05-17.
 */
public class VersionCommand implements DCommand {
    private String command = "versions";
    private String usage = "/versions";
    private String description = "Returns all the versions of the bot.";

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
    public void trigger(DContext context) {
        List<String> versions = new ArrayList<>();

        File folder = DiscordBot.moduleFileUtil.getPersonalDir();
        folder = new File(folder, "Changelogs");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            new ErrorMessage(context.getMember(), "There are no versions. weird.").sendMessage(context.getChannel());
            return;
        }

        for (File listOfFile : listOfFiles) {
            if (!listOfFile.isFile()) continue;
            versions.add(listOfFile.getName().replace("Changelog_", "").replace(".json", ""));
        }

        StringBuilder str = new StringBuilder();
        for (String version : versions) {
            str.append("- ").append(version).append("\n");
        }
        new TextMessage().setMention(context.getMember()).addText("All VERSION for /changelog [VERSION]")
                .addField("Versions", str.toString(), false).sendMessage(context.getChannel());
    }
}