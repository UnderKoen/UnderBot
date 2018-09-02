package nl.underkoen.chatbot.models;

/**
 * Created by Under_Koen on 02/09/2018.
 */
public interface Member<S extends Server, U extends User> {
    S getServer();

    U getUser();

    int getHighestRank();
}
