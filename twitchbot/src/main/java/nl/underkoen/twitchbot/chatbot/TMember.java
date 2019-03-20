package nl.underkoen.twitchbot.chatbot;

import nl.makertim.functionalmaker.irc.message.TwitchMessage;
import nl.underkoen.chatbot.models.Member;
import nl.underkoen.twitchbot.Roles;

/**
 * Created by Under_Koen on 02/09/2018.
 */
public class TMember implements Member<TServer, TUser> {
    private TUser user;
    private TServer server;
    private boolean mod;
    private boolean sub;
    private boolean turbo;
    private boolean owner;

    protected TMember(TUser user, TServer server) {
        this.user = user;
        this.server = server;
        this.mod = false;
        this.sub = false;
        this.turbo = false;
        this.owner = user.getName().equalsIgnoreCase(server.getName().replace("#", ""));
    }

    protected TMember(TUser user, TServer channel, TwitchMessage message) {
        this.user = user;
        this.server = channel;
        updateMember(message);
    }

    @Override
    public TServer getServer() {
        return server;
    }

    @Override
    public TUser getUser() {
        return user;
    }

    @Override
    public int getHighestRank() {
        return Roles.getRole(this).role;
    }

    public boolean isMod() {
        return mod;
    }

    public boolean isSub() {
        return sub;
    }

    public boolean isTurbo() {
        return turbo;
    }

    public boolean isOwner() {
        return owner;
    }

    protected void updateMember(TwitchMessage message) {
        this.mod = message.isMod();
        this.sub = message.isSub();
        this.turbo = message.isTurbo();
        this.owner = message.getRoomId().equals(message.getUserId());
    }
}
