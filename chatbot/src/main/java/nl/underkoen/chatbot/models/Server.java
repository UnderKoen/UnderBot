package nl.underkoen.chatbot.models;

import java.util.List;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public interface Server<CH extends Channel, MEM extends Member, U extends User> {
    String getName();

    List<CH> getChannels();

    MEM getMember(U user);
}