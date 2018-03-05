package nl.underkoen.underbot.messages;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

/**
 * Created by Under_Koen on 03/03/2018.
 */
public abstract class Message {
    public abstract String getMethod();

    public void init() {
    }

    public abstract void onCall(SocketIOClient client, String data, AckRequest ackSender) throws Exception;
}
