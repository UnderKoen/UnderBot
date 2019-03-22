package nl.underkoen.discordbot;

import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import nl.underkoen.chatbot.CommandHandler;
import nl.underkoen.chatbot.models.Command;
import nl.underkoen.chatbot.models.RankAccessible;
import nl.underkoen.discordbot.entities.DChannel;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.entities.DMessage;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.RoleUtil;

import java.util.Arrays;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public class DCommandHandler extends CommandHandler<Command<DContext>, DContext, DMessage>  implements EventListener {
    @Override
    public void onEvent(Event event) {
        if (event instanceof ReadyEvent) {
            ReadyEvent readyEvent = (ReadyEvent) event;
            onReady(readyEvent);
        } else if (event instanceof MessageReceivedEvent) {
            MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
            handle(messageReceivedEvent);
        }
    }

    public void onReady(ReadyEvent event) {
        DiscordBot.client.getPresence().setPresence(OnlineStatus.ONLINE, Game.playing("/help -> for help"));
        setup();
    }

    public void handle(MessageReceivedEvent event) {
        try {
            check(new DMessage(event.getMessage()));
        } catch (Exception err) {
            err.printStackTrace();
            new ErrorMessage(DMember.getMember(event.getGuild(), event.getAuthor()), "Something went wrong.")
                    .sendMessage(DChannel.getChannel(event.getTextChannel()));
        }
    }

    @Override
    public boolean canRun(DContext context) {
        DMessage message = context.getMessage();
        DiscordBot.timer.schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        message.getMessage().delete().complete();
                    }
                },
                TimeUnit.MINUTES.toMillis(1)
        );

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
