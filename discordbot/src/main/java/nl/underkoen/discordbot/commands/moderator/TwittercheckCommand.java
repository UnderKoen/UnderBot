package nl.underkoen.discordbot.commands.moderator;

import nl.underkoen.discordbot.Main;
import nl.underkoen.discordbot.Roles;
import nl.underkoen.discordbot.commands.Command;
import nl.underkoen.discordbot.entities.CommandContext;
import nl.underkoen.discordbot.threads.Twittercheck;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Under_Koen on 09-05-17.
 */
public class TwittercheckCommand implements Command {
    private String command = "twittercheck";
    private String usage = "/twittercheck [twitter_name] (textChannel)";
    private String description = "Enable/disable twittercheck for [twitter_name] in (textchannel).";
    private int minimumRole = Roles.MOD.role;

    private String consumerKey, consumerSecret, token, tokenSecret;

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
        consumerKey = Main.keys.getTwitterKeys()[0];
        consumerSecret = Main.keys.getTwitterKeys()[1];
        token = Main.keys.getTwitterKeys()[2];
        tokenSecret = Main.keys.getTwitterKeys()[3];
    }

    @Override
    public void run(CommandContext context) throws Exception {
        if (context.getRawArgs().length < 1 && !Twittercheck.check) {
            new ErrorMessage(context.getMember(), "This command needs arguments to work.")
                    .sendMessage(context.getChannel());
            return;
        }
        if (!Twittercheck.check) {
            Twittercheck.user = context.getArgs()[0];

            if (context.getArgs().length >= 2) {
                Pattern pattern = Pattern.compile("<#(\\d+)>");
                Matcher matcher = pattern.matcher(context.getRawArgs()[1]);
                matcher.find();
                try {
                    Twittercheck.channel = context.getGuild().getChannelByID(Long.parseLong(matcher.group(1)));
                } catch (Exception e) {
                    new ErrorMessage(context.getMember(), context.getRawArgs()[1] + " is not a valid channel.")
                            .sendMessage(context.getChannel());
                    return;
                }
            } else {
                Twittercheck.channel = context.getChannel();
            }
        }
        Twittercheck.check = !Twittercheck.check;
        Twittercheck.start(consumerKey, consumerSecret, token, tokenSecret);
        if (Twittercheck.check) {
            new TextMessage().setMention(context.getMember())
                    .addText("Enabled twitter check for " + Twittercheck.channel.mention() + ".")
                    .sendMessage(context.getChannel());
        } else {
            new TextMessage().setMention(context.getMember())
                    .addText("Disabled twitter check for " + Twittercheck.channel.mention() + ".")
                    .sendMessage(context.getChannel());
        }

    }
}
