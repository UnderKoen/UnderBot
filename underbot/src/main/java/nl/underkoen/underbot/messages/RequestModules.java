package nl.underkoen.underbot.messages;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import lombok.Getter;
import nl.underkoen.underbot.handlers.ModuleHandler;
import nl.underkoen.underbot.utils.MessageBuilder;

/**
 * Created by Under_Koen on 03/03/2018.
 */
public class RequestModules extends Message {
    @Getter
    private String method = "requestModules";

    private ModuleHandler moduleHandler;

    public RequestModules(ModuleHandler moduleHandler) {
        this.moduleHandler = moduleHandler;
    }

    @Override
    public void onCall(SocketIOClient client, String data, AckRequest ackSender) {
        client.sendEvent("message", MessageBuilder.getModulesMessage(moduleHandler.getModules()));
    }
}
