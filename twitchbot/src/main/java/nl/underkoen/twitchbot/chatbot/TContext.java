package nl.underkoen.twitchbot.chatbot;

import nl.underkoen.chatbot.models.Command;
import nl.underkoen.chatbot.models.Context;

/**
 * Created by Under_Koen on 03/09/2018.
 */
public class TContext implements Context<TChannel, TMember, TMessage, TServer, TUser> {
    private TChannel channel;
    private Command command;
    private TMember member;
    private TMessage message;
    private TServer server;
    private TUser user;
    private String[] args;

    public TContext(Command command, TMessage message, String[] args) {
        this.command = command;
        this.message = message;
        this.channel = message.getChannel();
        this.member = message.getMember();
        this.server = message.getServer();
        this.user = message.getUser();
        this.args = args;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    @Override
    public TChannel getChannel() {
        return channel;
    }

    @Override
    public Command getCommand() {
        return command;
    }

    @Override
    public TMember getMember() {
        return member;
    }

    @Override
    public TMessage getMessage() {
        return message;
    }

    @Override
    public TServer getServer() {
        return server;
    }

    @Override
    public TUser getUser() {
        return user;
    }
}
