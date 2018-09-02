package nl.underkoen.underbot.messages;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

/**
 * Created by Under_Koen on 03/03/2018.
 */
public interface Message {
    String getMethod();

    default void init() {
    }

    void onCall(SocketIOClient client, String data, AckRequest ackSender) throws Exception;
}
