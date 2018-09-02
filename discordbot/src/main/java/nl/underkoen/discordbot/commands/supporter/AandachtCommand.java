package nl.underkoen.discordbot.commands.supporter;

import nl.underkoen.chatbot.models.RankAccessible;
import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.Roles;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.entities.DUser;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Under_Koen on 03-05-17.
 */
public class AandachtCommand implements DCommand, RankAccessible {
    private String command = "aandacht";
    private String usage = "/aandacht";
    private String description = "Sends a line from AANDACHT - Kud";
    private int minimumRole = Roles.SUPPORTER.role;

    @Override
    public int getMinimumRank() {
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
    public void setup() {
        DiscordBot.moduleFileUtil.copyResourceToPersonalDir("Aandacht.txt");
        aandacht = getAandacht();
    }

    private ArrayList<String> getAandacht() {

        ArrayList<String> result = new ArrayList<>();

        try {
            Collections.addAll(result, DiscordBot.moduleFileUtil.getContent("Aandacht.txt").split("\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private ArrayList<String> aandacht;
    private int current = 0;
    private DUser last;

    @Override
    public void trigger(DContext context) {
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
