package nl.underkoen.discordbot.commands.supporter;

import nl.underkoen.discordbot.Roles;
import nl.underkoen.discordbot.commands.Command;
import nl.underkoen.discordbot.entities.CommandContext;
import nl.underkoen.discordbot.utils.Messages.TextMessage;
import nl.underkoen.underbot.utils.FileUtilOld;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.Collections;

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
        FileUtilOld.makeDuplicate("Aandacht.txt");
        aandacht = getAandacht();
    }

    private ArrayList<String> getAandacht() {

        ArrayList<String> result = new ArrayList<>();

        try {
            Collections.addAll(result, FileUtilOld.getFileInput("Aandacht.txt").split("\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if (current == aandacht.size() - 1) {
                current = 0;
            } else {
                current = current + 1;
            }
        }
    }
}
