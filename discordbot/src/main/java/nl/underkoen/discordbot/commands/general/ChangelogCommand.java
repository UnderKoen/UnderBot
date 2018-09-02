package nl.underkoen.discordbot.commands.general;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

/**
 * Created by Under_Koen on 23-04-17.
 */
public class ChangelogCommand implements DCommand {
    private String command = "changelog";
    private String usage = "/changelog (VERSION)";
    private String description = "Returns the changelog of a VERSION.";

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
    public void setup() {
        DiscordBot.moduleFileUtil.copyResourceToPersonalDir("Changelog.json", "Changelogs/Changelog_" + DiscordBot.VERSION + ".json");
    }

    private String getChangelog(String version) {
        return DiscordBot.moduleFileUtil.getContent("/Changelogs/Changelog_" + version + ".json");
    }

    @Override
    public void trigger(DContext context) {
        String version = DiscordBot.VERSION;
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
        TextMessage message = new TextMessage().setMention(context.getMember()).addText("The changelog of VERSION " + version);
        message.addField("Added", added.toString(), false);
        message.addField("Removed", removed.toString(), false);
        message.addField("Fixed", fixed.toString(), false);
        message.addField("Todo", todo.toString(), false);
        message.sendMessage(context.getChannel());
    }
}
