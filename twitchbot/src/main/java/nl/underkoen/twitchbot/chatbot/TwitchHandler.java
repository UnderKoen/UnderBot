package nl.underkoen.twitchbot.chatbot;

import nl.makertim.functionalmaker.irc.irc.Command;
import nl.makertim.functionalmaker.irc.irc.IRCTwitch;
import nl.makertim.functionalmaker.irc.message.TwitchMessage;
import nl.underkoen.chatbot.CommandHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static nl.makertim.functionalmaker.irc.IRCConnection.connectIRC;

/**
 * Created by Under_Koen on 02/09/2018.
 */
public class TwitchHandler {
    private IRCTwitch client;
    private CommandHandler commandHandler;
    private boolean connected = false;
    private Runnable onConnect;

    public TwitchHandler(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    public void init(String username, String auth) {
        Optional<IRCTwitch> t = connectIRC(IRCTwitch.class, "irc.chat.twitch.tv", 6667, Throwable::printStackTrace);
        if (t.isPresent()) {
            client = t.get();
            client.login(username, username, auth);
            client.setOnConnect(this::onConnect);
            client.onGlobal(this::onMessage);
        } else {
            throw new RuntimeException("Failed to connect to IRC.");
        }
    }

    public void start() {
        client.startListeningThreaded();
    }

    public IRCTwitch getClient() {
        return client;
    }

    public TServer joinServer(String serverName) {
        if (hasServer(serverName)) throw new IllegalArgumentException("Already connected to this channel");
        client.joinChannel(serverName);
        return addServer(serverName);
    }

    public void leaveServer(TServer server) {
        if (!hasServer(server)) throw new IllegalArgumentException("Not connected to this channel");
        removeServer(server);
        client.part(server.getName());
    }

    private void onConnect() {
        connected = true;
        if (onConnect != null) onConnect.run();
    }

    public void setOnConnect(Runnable onConnect) {
        if (isConnected()) throw new RuntimeException("Client is already connected.");
        this.onConnect = onConnect;
    }

    public boolean isConnected() {
        return connected;
    }

    private void onMessage(TwitchMessage message) {
        if (message.getCommand() == Command.PRIVMSG) {
            commandHandler.check(new TMessage(message, this));
        }
    }

    //SERVERS
    private List<TServer> servers = new ArrayList<>();

    public TServer getServer(String name) {
        return servers.stream()
                .filter(server -> server.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public boolean hasServer(String name) {
        return servers.stream()
                .anyMatch(server -> server.getName().equalsIgnoreCase(name));
    }

    protected boolean hasServer(TServer server) {
        return servers.contains(server);
    }

    protected TServer addServer(String name) {
        if (hasServer(name)) throw new IllegalArgumentException("Server already exists");
        TServer server = new TServer(name, this);
        servers.add(server);
        return server;
    }

    protected void removeServer(TServer server) {
        if (server == null) throw new IllegalArgumentException("Server can't be null");
        if (!hasServer(server)) throw new IllegalArgumentException("Server doesn't exists");
        servers.remove(server);
    }

    protected List<TServer> getServers() {
        return servers;
    }

    //CHANNELS
    public TChannel getChannel(String name) {
        return getServer(name).getChannel();
    }

    //USERS
    private List<TUser> users = new ArrayList<>();

    protected TUser getUser(String name) {
        return users.stream()
                .filter(tUser -> tUser.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(new TUser(name, this));
    }

    protected boolean hasUser(String name) {
        return users.stream()
                .anyMatch(user -> user.getName().equalsIgnoreCase(name));
    }

    protected boolean hasUser(TUser user) {
        return users.contains(user);
    }

    protected List<TUser> getUsers() {
        return users;
    }
}
