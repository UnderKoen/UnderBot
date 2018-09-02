package nl.underkoen.chatbot.models;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public interface Command<CON extends Context> {
    String getPrefix();

    String getCommand();

    String getUsage();

    String getDescription();

    default String[] getAliases() {
        return new String[0];
    }

    default void setup() {
    }

    default void teardown() {
    }

    void trigger(CON context);
}