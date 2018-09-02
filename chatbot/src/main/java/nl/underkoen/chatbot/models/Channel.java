package nl.underkoen.chatbot.models;

/**
 * Created by Under_Koen on 02/09/2018.
 */
public interface Channel<S extends Server> {
    String getName();

    S getServer();

    void sendMessage(String message);
}
