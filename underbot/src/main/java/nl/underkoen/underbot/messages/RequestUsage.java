package nl.underkoen.underbot.messages;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.Getter;
import nl.underkoen.underbot.models.Usage;
import nl.underkoen.underbot.utils.MessageBuilder;

/**
 * Created by Under_Koen on 03/03/2018.
 */
public class RequestUsage extends Message {
    @Getter
    private String method = "requestUsage";

    @Override
    public void onCall(SocketIOClient client, String data, AckRequest ackSender) {
        client.sendEvent("message", MessageBuilder.getUsageMessage(Usage.getCurrentUsage()));
    }
}
