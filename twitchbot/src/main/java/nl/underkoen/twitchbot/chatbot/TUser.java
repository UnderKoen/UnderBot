package nl.underkoen.twitchbot.chatbot;

import nl.makertim.functionalmaker.irc.irc.IRCTwitch;
import nl.underkoen.chatbot.models.User;

/**
 * Created by Under_Koen on 01/09/2018.
 */
public class TUser implements User {
    private TwitchHandler handler;
    private IRCTwitch client;
    private String name;

    protected TUser(String name, TwitchHandler handler) {
        this.handler = handler;
        this.client = handler.getClient();
        this.name = name;
    }

    public void whisper(String message) {
        client.privateMessage(name, message);
    }

    @Override
    public String getName() {
        return name;
    }
}
