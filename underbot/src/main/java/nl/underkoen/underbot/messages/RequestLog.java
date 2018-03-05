package nl.underkoen.underbot.messages;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.Getter;
import nl.underkoen.underbot.utils.MessageBuilder;
import nl.underkoen.underbot.utils.WebPrintStream;

/**
 * Created by Under_Koen on 03/03/2018.
 */
public class RequestLog extends Message {
    @Getter
    private String method = "requestLog";

    @Override
    public void onCall(SocketIOClient client, String data, AckRequest ackSender) throws Exception {
        client.sendEvent("message", MessageBuilder.getLogMessage(WebPrintStream.logFile));
    }
}