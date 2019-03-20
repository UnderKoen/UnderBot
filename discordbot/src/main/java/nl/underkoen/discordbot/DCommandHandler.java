package nl.underkoen.discordbot;

import nl.underkoen.chatbot.CommandHandler;
import nl.underkoen.chatbot.models.Command;
import nl.underkoen.chatbot.models.RankAccessible;
import nl.underkoen.discordbot.entities.DChannel;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.entities.DMessage;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.RoleUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;

import java.util.Arrays;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public class DCommandHandler extends CommandHandler<Command<DContext>, DContext, DMessage> {
    @EventSubscriber
    public void onReady(ReadyEvent event) {
        DiscordBot.client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, "/help -> for help");
        setup();
    }

    @EventSubscriber
    public void handle(MessageReceivedEvent event) {
        try {
            check(new DMessage(event.getMessage()));
        } catch (Exception err) {
            err.printStackTrace();
            new ErrorMessage(DMember.getMember(event.getGuild(), event.getAuthor()), "Something went wrong.")
                    .sendMessage(DChannel.getChannel(event.getChannel()));
        }
    }

    @Override
    public boolean canRun(DContext context) {
        DMessage message = context.getMessage();
        message.getMessage().delete();

        DMember member = context.getMember();
        Command command = context.getCommand();
        if (command instanceof RankAccessible) {
            RankAccessible rank = (RankAccessible) command;
            if (member.getHighestRank() < rank.getMinimumRank()) {
                new ErrorMessage(member, "The minimum role for /" + command.getCommand() + " is " +
                        RoleUtil.getRole(context.getServer(), rank.getMinimumRank()).getName()).sendMessage(context.getChannel());
                return false;
            }
        }
        return true;
    }

    @Override
    public DContext toContext(Command command, DMessage message) {
        String[] args = message.getRawContent().split(" ");
        args = Arrays.copyOfRange(args, 1, args.length);
        String[] rawArgs = message.getRawContent().split(" ");
        rawArgs = Arrays.copyOfRange(rawArgs, 1, rawArgs.length);
        return new DContext(command, message, args, rawArgs);
    }

    @Override
    public void setup() {
        super.setup();
    }

    @Override
    public void teardown() {
        super.teardown();
    }
}
