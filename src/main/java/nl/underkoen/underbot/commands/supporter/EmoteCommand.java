package nl.underkoen.underbot.commands.supporter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.underkoen.underbot.Roles;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.utils.FileUtil;
import nl.underkoen.underbot.utils.Messages.ErrorMessage;
import nl.underkoen.underbot.utils.Messages.TextMessage;

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
        FileUtil.makeDuplicate("Emote.json");
    }

    @Override
    public void run(CommandContext context) throws Exception {
        if (context.getArgs().length == 0) {
            new ErrorMessage(context.getMember(), "No text to change to emote text")
                    .sendMessage(context.getChannel());
            return;
        }
        StringBuilder args = new StringBuilder();
        for (String arg: context.getArgs()) {
            args.append(arg).append(" ");
        }
        args = new StringBuilder(args.toString().trim().toLowerCase());
        StringBuilder text = new StringBuilder();
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(FileUtil.getFile("Emote.Json")).getAsJsonObject();
        TextMessage msg = new TextMessage().setMention(context.getMember());
        for (char Char : args.toString().toCharArray()) {
            String add = (o.has(Char + "")) ? o.get(Char + "").getAsString() : o.get("none").getAsString();
            text.append(add).append(" ");
        }
        if (text.length() < 2048) {
            msg.addText(text.toString()).sendMessage(context.getChannel());
        } else {
            new TextMessage().setMention(context.getMember())
                    .addText("The amount of char's in emote form is over 2048 and can't be post sorry.")
                    .sendMessage(context.getChannel());
        }
    }
}
