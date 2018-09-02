package nl.underkoen.chatbot.models;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public interface Context<CH extends Channel, MEM extends Member, MES extends Message, S extends Server, U extends User> {
    String[] getArgs();

    CH getChannel();

    Command getCommand();

    MEM getMember();

    MES getMessage();

    S getServer();

    U getUser();
}
