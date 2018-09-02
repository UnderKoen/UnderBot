package nl.underkoen.discordbot.entities;

import nl.underkoen.chatbot.models.Message;
import sx.blah.discord.handle.obj.IMessage;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public class DMessage implements Message<DChannel, DMember, DServer, DUser> {
    private IMessage message;
    private DUser user;
    private DChannel channel;
    private DServer server;
    private DMember member;

    public DMessage(IMessage message) {
        this.message = message;
        this.user = DUser.getUser(message.getAuthor());
        this.channel = DChannel.getChannel(message.getChannel());
        this.server = DServer.getServer(message.getGuild());
        this.member = DMember.getMember(server, user);
    }

    @Override
    public String getContent() {
        return message.getFormattedContent();
    }

    public String getRawContent() {
        return message.getContent();
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
    public DServer getServer() {
        return server;
    }

    @Override
    public DMember getMember() {
        return member;
    }

    public IMessage getMessage() {
        return message;
    }
}
