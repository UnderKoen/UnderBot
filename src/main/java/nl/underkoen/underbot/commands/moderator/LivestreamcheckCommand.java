package nl.underkoen.underbot.commands.moderator;

import nl.underkoen.underbot.Roles;
import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.threads.Livestreamcheck;
import nl.underkoen.underbot.utils.Messages.ErrorMessage;
import nl.underkoen.underbot.utils.Messages.TextMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Under_Koen on 09-05-17.
 */
public class LivestreamcheckCommand implements Command {
    private String command = "livestreamcheck";
    private String usage = "/livestreamcheck (textChannel)]";
    private String description = "Enable/disable livestreamcheck in (textchannel).";
    private int minimumRole = Roles.MOD.role;

    private boolean checking = false;
    private Livestreamcheck thread;

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
    }

    @Override
    public void run(CommandContext context) throws Exception {
        if (!checking) {
            checking = true;
            thread = new Livestreamcheck();

            if (context.getArgs().length >= 1) {
                Pattern pattern = Pattern.compile("<#(\\d+)>");
                Matcher matcher = pattern.matcher(context.getRawArgs()[0]);
                matcher.find();
                try {
                    thread.setChannel(context.getGuild().getChannelByID(Long.parseLong(matcher.group(1))));
                } catch (Exception e) {
                    new ErrorMessage(context.getMember(), context.getRawArgs()[0] + " is not a valid channel.")
                            .sendMessage(context.getChannel());
                    return;
                }
            } else {
                thread.setChannel(context.getChannel());
            }
            thread.start();
            if (thread.check) {
                new TextMessage().setMention(context.getMember())
                        .addText("Enabled livestream check for " + thread.channel.mention() + ".")
                        .sendMessage(context.getChannel());
            } else {
                new TextMessage().setMention(context.getMember())
                        .addText("Disabled livestream check for " + thread.channel.mention() + ".")
                        .sendMessage(context.getChannel());
            }
        } else {
            thread.stopCheck();
            checking = false;
        }

    }
}
