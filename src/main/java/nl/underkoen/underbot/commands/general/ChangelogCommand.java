package nl.underkoen.underbot.commands.general;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.utils.FileUtil;
import nl.underkoen.underbot.utils.Messages.ErrorMessage;
import nl.underkoen.underbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 23-04-17.
 */
public class ChangelogCommand implements Command {
    private String command = "changelog";
    private String usage = "/changelog (version)";
    private String description = "Returns the changelog of a version.";

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
        FileUtil.makeDuplicate("Changelog.json", "Changelogs/Changelog_" + Main.version + ".json");
    }

    private String getChangelog(String version) throws Exception {
        return FileUtil.getFile("/Changelogs/Changelog_" + version + ".json");
    }

    @Override
    public void run(CommandContext context) {
        String version = Main.version;
        if (context.getArgs().length > 0) {
            version = context.getArgs()[0];
        }
        String changelog;
        try {
            changelog = getChangelog(version);
        } catch (Exception e) {
            new ErrorMessage(context.getMember(), "The changelog for " + version + " does not exist")
                    .sendMessage(context.getChannel());
            return;
        }
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(changelog).getAsJsonObject();
        StringBuilder added = new StringBuilder();
        for (JsonElement obj : o.get("ADDED").getAsJsonArray()) {
            added.append("- ").append(obj.getAsString()).append("\n");
        }
        StringBuilder removed = new StringBuilder();
        for (JsonElement obj : o.get("REMOVED").getAsJsonArray()) {
            removed.append("- ").append(obj.getAsString()).append("\n");
        }
        StringBuilder fixed = new StringBuilder();
        for (JsonElement obj : o.get("FIXED").getAsJsonArray()) {
            fixed.append("- ").append(obj.getAsString()).append("\n");
        }
        StringBuilder todo = new StringBuilder();
        for (JsonElement obj : o.get("TODO").getAsJsonArray()) {
            todo.append("- ").append(obj.getAsString()).append("\n");
        }
        TextMessage message = new TextMessage().setMention(context.getMember()).addText("The changelog of version " + version);
        message.addField("Added", added.toString(), false);
        message.addField("Removed", removed.toString(), false);
        message.addField("Fixed", fixed.toString(), false);
        message.addField("Todo", todo.toString(), false);
        message.sendMessage(context.getChannel());
    }
}
