package nl.underkoen.underbot;

import nl.underkoen.underbot.commands.Command;
import nl.underkoen.underbot.entities.Member;
import nl.underkoen.underbot.entities.impl.CommandContextImpl;
import nl.underkoen.underbot.entities.impl.MemberImpl;
import nl.underkoen.underbot.exceptions.AlreadyInitializedException;
import nl.underkoen.underbot.utils.Messages.ErrorMessage;
import nl.underkoen.underbot.utils.RoleUtil;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Under_Koen on 19-04-17.
 */
public class CommandHandler {
    private HashMap<String, Command> commands;
    private HashMap<String, String> aliases;
    private String prefix;

    public void initializeCommand(Command command) {
        String commandName = command.getCommand();
        if (commands.containsKey(commandName) || aliases.containsKey(commandName)) throw new AlreadyInitializedException();
        commands.put(commandName, command);
        String[] aliases = command.getAliases();
        for (String alias: aliases) {
            if (this.aliases.containsKey(alias) || commands.containsKey(alias)) continue;
            this.aliases.put(alias, commandName);
        }
    }

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        Main.client.changePlayingText("/help -> for help");
        for (Command command: commands.values()) {
            try {
                command.setup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CommandHandler(String prefix) {
        commands = new HashMap<String, Command>();
        aliases = new HashMap<String, String>();
        this.prefix = prefix;
    }

    public ArrayList<Command> getAllCommands() {
        return new ArrayList<Command>(commands.values());
    }

    public String getPrefix() {
        return prefix;
    }

    @EventSubscriber
    public void handle(MessageReceivedEvent event) {
        String messageContent = event.getMessage().getFormattedContent();
        String messageRawContent = event.getMessage().getContent();

        if (!messageContent.startsWith(prefix)) return;

        CommandContextImpl context = new CommandContextImpl();

        context.setPrefix(prefix);

        String commandName = messageContent.split(" ")[0].trim().replaceFirst(prefix, "").toLowerCase();
        context.setCommand(commandName);

        if (!commands.containsKey(commandName) && !aliases.containsKey(commandName)) return;

        if (!commands.containsKey(commandName) && aliases.containsKey(commandName)) commandName = aliases.get(commandName);

        Command command = commands.get(commandName);
        IUser user = event.getAuthor();
        IChannel channel = event.getChannel();
        IGuild guild = event.getGuild();
        Member member = new MemberImpl(guild, user);
        IMessage message = event.getMessage();

        if (RoleUtil.getHighestRole(member).getPosition() < command.getMinimumRole()) {
            new ErrorMessage(member, "The minimum role for /" + commandName + " is " +
                    RoleUtil.getRole(event.getGuild(), command.getMinimumRole()).getName()).sendMessage(event.getChannel());
            event.getMessage().delete();
            return;
        }

        String[] argsTest = messageContent.split(" ");
        ArrayList<String> args = new ArrayList<String>();
        for (String arg : argsTest) {
            if (argsTest[0] == arg) continue;
            args.add(arg);
        }
        context.setArgs(args.toArray(new String[0]));

        String[] rawArgsTest = messageRawContent.split(" ");
        ArrayList<String> rawArgs = new ArrayList<String>();
        for (String rawArg : rawArgsTest) {
            if (rawArgsTest[0] == rawArg) continue;
            rawArgs.add(rawArg);
        }
        context.setRawArgs(rawArgs.toArray(new String[0]));

        context.setUser(user);

        context.setChannel(channel);

        context.setGuild(guild);

        context.setMember(member);

        context.setMessage(message);

        try {
            command.run(context);
        } catch (Exception ex) {
            new ErrorMessage(context.getMember(), "An error occured").sendMessage(context.getChannel());
            ex.printStackTrace();
        }

        try {
            message.delete();
        } catch (Exception ex) {
        }
    }
}
