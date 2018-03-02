package nl.underkoen.underbot.handlers;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import nl.underkoen.underbot.models.Usage;
import nl.underkoen.underbot.utils.MessageBuilder;
import nl.underkoen.underbot.utils.WebPrintStream;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.net.URISyntaxException;

/**
 * Created by Under_Koen on 02/03/2018.
 */
public class SocketHandler {
    @Getter
    private SocketIOServer server;

    public SocketHandler() {
        Configuration config = new Configuration();
        config.setHostname("localhost");
        config.setPort(9092);

        server = new SocketIOServer(config);
        server.addConnectListener(client -> onConnection(client));
        server.addDisconnectListener(client -> onDisconnection(client));
        server.addEventListener("message", String.class, (client, data, ackSender) -> onMessage(client, data, ackSender));

        PrintStream out = new WebPrintStream(server, MessageBuilder.ConsoleType.LOG);
        PrintStream err = new WebPrintStream(server, MessageBuilder.ConsoleType.ERR);
        System.setOut(out);
        System.setErr(err);

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }

    public void onConnection(SocketIOClient client) {

    }

    public void onDisconnection(SocketIOClient client) {

    }

    public void onMessage(SocketIOClient client, String data, AckRequest ackSender) throws FileNotFoundException, URISyntaxException {
        JsonObject json = new JsonParser().parse(data).getAsJsonObject();
        switch (json.get("method").getAsString()) {
            case "requestUsage":
                client.sendEvent("message", MessageBuilder.getUsageMessage(Usage.getCurrentUsage()));
                break;
            case "requestLog":
                client.sendEvent("message", MessageBuilder.getLogMessage(WebPrintStream.logFile));
                break;
        }
    }
}
