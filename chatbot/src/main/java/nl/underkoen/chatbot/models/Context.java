package nl.underkoen.chatbot.models;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public interface Context<CH extends Channel, COM extends Command, MEM extends Member, MES extends Message, S extends Server, U extends User> {
    String[] getArgs();

    CH getChannel();

    COM getCommand();

    MEM getMember();

    MES getMessage();

    S getServer();

    U getUser();
}
