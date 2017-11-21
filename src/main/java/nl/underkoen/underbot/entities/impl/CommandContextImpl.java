package nl.underkoen.underbot.entities.impl;

import nl.underkoen.underbot.entities.CommandContext;
import nl.underkoen.underbot.entities.Member;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

/**
 * Created by Under_Koen on 19-04-17.
 */
public class CommandContextImpl implements CommandContext {

    private String prefix;
    private String command;
    private String[] args;
    private String[] rawArgs;
    private IUser user;
    private IChannel channel;
    private IGuild guild;
    private Member member;
    private IMessage message;

    @Override
    public String getPrefix() {
        return prefix;
    }

    public CommandContextImpl setPrefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    @Override
    public String getCommand() {
        return command;
    }

    public CommandContextImpl setCommand(String command) {
        this.command = command;
        return this;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    public CommandContextImpl setArgs(String[] args) {
        this.args = args;
        return this;
    }

    @Override
    public String[] getRawArgs() {
        return rawArgs;
    }

    public CommandContextImpl setRawArgs(String[] rawArgs) {
        this.rawArgs = rawArgs;
        return this;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    public CommandContextImpl setUser(IUser user) {
        this.user = user;
        return this;
    }

    @Override
    public IChannel getChannel() {
        return channel;
    }

    public CommandContextImpl setChannel(IChannel channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public IGuild getGuild() {
        return guild;
    }

    public CommandContextImpl setGuild(IGuild guild) {
        this.guild = guild;
        return this;
    }

    @Override
    public Member getMember() {
        return member;
    }

    public CommandContextImpl setMember(Member member) {
        this.member = member;
        return this;
    }

    @Override
    public IMessage getMessage() {
        return message;
    }

    public CommandContextImpl setMessage(IMessage message) {
        this.message = message;
        return this;
    }
}
