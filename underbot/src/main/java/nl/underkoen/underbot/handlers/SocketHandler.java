package nl.underkoen.underbot.handlers;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.messages.Message;
import nl.underkoen.underbot.utils.MessageBuilder;
import nl.underkoen.underbot.utils.WebPrintStream;

import java.io.PrintStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Under_Koen on 02/03/2018.
 */
public class SocketHandler {
    @Getter
    private SocketIOServer server;

    private Map<String, Message> messages;

    @Getter
    private Map<UUID, Boolean> loggedIn;

    private boolean started = false;

    public SocketHandler() {
        JsonObject configJson = new JsonParser().parse(Main.assetHandler.getFileUtil().getContent("config.json")).getAsJsonObject();
        Configuration config = new Configuration();
        config.setPort(configJson.get("port").getAsInt());

        server = new SocketIOServer(config);
        server.addConnectListener(this::onConnection);
        server.addDisconnectListener(this::onDisconnection);
        server.addEventListener("message", String.class, this::onMessage);

        PrintStream out = new WebPrintStream(server, MessageBuilder.ConsoleType.LOG);
        PrintStream err = new WebPrintStream(server, MessageBuilder.ConsoleType.ERR);
        System.setOut(out);
        System.setErr(err);

        messages = new HashMap<>();
        loggedIn = new HashMap<>();
    }

    public boolean start() {
        if (started) return false;
        server.start();
        started = true;
        return true;
    }

    public boolean stop() {
        if (!started) return false;
        server.stop();
        return true;
    }

    /**
     * @return true if succesfully added, false when not.
     */
    public boolean addMessage(Message message) {
        if (messages.containsKey(message.getMethod().toLowerCase())) {
            return false;
        }
        messages.put(message.getMethod().toLowerCase(), message);
        message.init();
        return true;
    }

    /**
     * @return true if succesfully remove, false when not.
     */
    public boolean removeMessage(Message message) {
        return messages.remove(message.getMethod().toLowerCase(), message);
    }

    private void onConnection(SocketIOClient client) {
        loggedIn.put(client.getSessionId(), false);
    }

    private void onDisconnection(SocketIOClient client) {
        loggedIn.remove(client.getSessionId());
    }

    private void onMessage(SocketIOClient client, String data, AckRequest ackSender) {
        try {
            JsonObject json = new JsonParser().parse(data).getAsJsonObject();
            if (loggedIn.get(client.getSessionId())) {
                messages.get(json.get("method").getAsString().toLowerCase()).onCall(client, data, ackSender);
            } else {
                if (!json.get("method").getAsString().equalsIgnoreCase("login")) return;
                String credential = json.getAsJsonObject("params").get("credential").getAsString();
                JsonObject configJson = new JsonParser().parse(Main.assetHandler.getFileUtil().getContent("config.json")).getAsJsonObject();
                MessageDigest md = MessageDigest.getInstance("SHA-256");

                configJson.getAsJsonArray("users").forEach(jsonElement -> {
                    JsonObject jsonC = jsonElement.getAsJsonObject();
                    md.update((jsonC.get("password").getAsString() + jsonC.get("username").getAsString()).getBytes());
                    StringBuilder result = new StringBuilder();
                    for (byte byt : md.digest()) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
                    if (result.toString().equals(credential)) {
                        loggedIn.replace(client.getSessionId(), true);
                    }
                });
                client.sendEvent("message", MessageBuilder.getLoginMessage(loggedIn.get(client.getSessionId())));
            }
        } catch (Exception ignored) {
        }
    }
}
