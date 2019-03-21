package nl.underkoen.discordbot.entities;

import net.dv8tion.jda.core.entities.Message;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public class DMessage implements nl.underkoen.chatbot.models.Message<DChannel, DMember, DServer, DUser> {
    private Message message;
    private DUser user;
    private DChannel channel;
    private DServer server;
    private DMember member;

    public DMessage(Message message) {
        this.message = message;
        this.user = DUser.getUser(message.getAuthor());
        this.channel = DChannel.getChannel(message.getTextChannel());
        this.server = DServer.getServer(message.getGuild());
        this.member = DMember.getMember(server, user);
    }

    @Override
    public String getContent() {
        return message.getContentDisplay();
    }

    public String getRawContent() {
        return message.getContentRaw();
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

    public Message getMessage() {
        return message;
    }
}
