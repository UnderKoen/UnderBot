package nl.underkoen.twitchbot.chatbot;

import nl.makertim.functionalmaker.irc.message.TwitchMessage;
import nl.underkoen.chatbot.models.Message;

/**
 * Created by Under_Koen on 01/09/2018.
 */
public class TMessage implements Message<TChannel, TMember, TServer, TUser> {
    private TwitchHandler handler;
    private TwitchMessage message;
    private String content;
    private TServer server;
    private TUser user;
    private TMember member;

    protected TMessage(TwitchMessage message, TwitchHandler handler) {
        this.handler = handler;
        this.message = message;
        String[] contentInfo = message.getContent().split(" :", 2);
        this.server = handler.getServer(contentInfo[0]);
        this.content = contentInfo[1];
        this.user = handler.getUser(message.getNickname());
        this.member = server.getMember(user.getName());
        member.updateMember(message);
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public TChannel getChannel() {
        return server.getChannel();
    }

    @Override
    public TUser getUser() {
        return user;
    }

    @Override
    public TServer getServer() {
        return server;
    }

    @Override
    public TMember getMember() {
        return member;
    }

    public TwitchMessage getMessage() {
        return message;
    }
}
