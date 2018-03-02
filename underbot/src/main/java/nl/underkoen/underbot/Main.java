package nl.underkoen.underbot;

import nl.underkoen.underbot.handlers.ModuleHandler;
import nl.underkoen.underbot.handlers.SocketHandler;

/**
 * Created by Under_Koen on 02/03/2018.
 */
public class Main {
    public static SocketHandler socketHandler;
    public static ModuleHandler moduleHandler;

    public static void main(String args[]) throws Exception {
        socketHandler = new SocketHandler();
        moduleHandler = new ModuleHandler();
    }
}
