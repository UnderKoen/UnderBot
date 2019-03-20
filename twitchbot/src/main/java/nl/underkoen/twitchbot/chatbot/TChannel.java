package nl.underkoen.twitchbot.chatbot;

import nl.underkoen.chatbot.models.Channel;

/**
 * Created by Under_Koen on 03/09/2018.
 */
public class TChannel implements Channel<TServer> {
    private TServer server;

    protected TChannel(TServer server) {
        this.server = server;
    }

    @Override
    public String getName() {
        return server.getName();
    }

    @Override
    public TServer getServer() {
        return server;
    }

    @Override
    public void sendMessage(String message) {
        server.sendMessage(message);
    }
}
