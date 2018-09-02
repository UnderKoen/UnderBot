package nl.underkoen.chatbot.models;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public interface Message<CH extends Channel, MEM extends Member, S extends Server, U extends User> {
    String getContent();

    CH getChannel();

    MEM getMember();

    S getServer();

    U getUser();
}
