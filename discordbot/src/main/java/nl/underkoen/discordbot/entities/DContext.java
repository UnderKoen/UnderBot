package nl.underkoen.discordbot.entities;

import net.dv8tion.jda.core.entities.Message;
import nl.underkoen.chatbot.models.Command;
import nl.underkoen.chatbot.models.Context;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public class DContext implements Context<DChannel, Command<DContext>, DMember, DMessage, DServer, DUser> {
    private Command<DContext> command;
    private DChannel channel;
    private DMessage message;
    private DUser user;
    private DServer server;
    private DMember member;
    private String[] args;
    private String[] rawArgs;

    public DContext(Command<DContext> command, DMessage message, String[] args, String[] rawArgs) {
        this.command = command;
        this.message = message;
        Message iMessage = message.getMessage();
        this.user = DUser.getUser(iMessage.getAuthor());
        this.channel = DChannel.getChannel(iMessage.getTextChannel());
        this.server = DServer.getServer(iMessage.getGuild());
        this.member = DMember.getMember(this.server, this.user);
        this.args = args;
        this.rawArgs = rawArgs;
    }

    @Override
    public Command<DContext> getCommand() {
        return command;
    }

    @Override
    public String[] getArgs() {
        return args;
    }

    public String[] getRawArgs() {
        return rawArgs;
    }

    @Override
    public DMessage getMessage() {
        return message;
    }

    @Override
    public DUser getUser() {
        return user;
    }

    @Override
    public DChannel getChannel() {
        return channel;
    }

    @Override
    public DMember getMember() {
        return member;
    }

    @Override
    public DServer getServer() {
        return server;
    }
}
