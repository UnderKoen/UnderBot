package nl.underkoen.twitchbot.chatbot;

import nl.makertim.functionalmaker.irc.irc.IRCTwitch;
import nl.makertim.functionalmaker.irc.message.TwitchMessage;
import nl.underkoen.chatbot.models.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Under_Koen on 01/09/2018.
 */
public class TServer implements Server<TChannel, TMember, TUser> {
    private TwitchHandler handler;
    private IRCTwitch client;
    private String channelName;
    private List<TMember> members;
    private TChannel channel;

    protected TServer(String channelName, TwitchHandler handler) {
        this.handler = handler;
        this.client = handler.getClient();
        this.channelName = channelName;
        this.members = new ArrayList<>();
        this.channel = (new TChannel(this));
    }

    protected TMember getMember(String name) {
        return members.stream()
                .filter(member -> member.getUser().getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(new TMember(handler.getUser(name), this));
    }

    protected boolean hasMember(String name) {
        return members.stream()
                .anyMatch(member -> member.getUser().getName().equalsIgnoreCase(name));
    }

    protected boolean hasUser(TMember member) {
        return members.contains(member);
    }

    protected List<TMember> getMembers() {
        return members;
    }

    protected void onMessage(TwitchMessage message) {
        TMember member = getMember(message.getNickname());
        member.updateMember(message);
    }

    @Override
    public String getName() {
        return channelName;
    }

    @Override
    public List<TChannel> getChannels() {
        return Collections.singletonList(channel);
    }

    public TChannel getChannel() {
        return channel;
    }

    @Override
    public TMember getMember(TUser user) {
        return getMember(user.getName());
    }

    protected void sendMessage(String message) {
        message = message.replace("\n", " ");
        client.privateMessage(channelName, message);
    }
}
