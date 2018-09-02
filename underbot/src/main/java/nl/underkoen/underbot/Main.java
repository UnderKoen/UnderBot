package nl.underkoen.underbot;

import nl.underkoen.underbot.handlers.AssetHandler;
import nl.underkoen.underbot.handlers.ModuleHandler;
import nl.underkoen.underbot.handlers.SocketHandler;
import nl.underkoen.underbot.messages.ModuleAction;
import nl.underkoen.underbot.messages.RequestLog;
import nl.underkoen.underbot.messages.RequestModules;
import nl.underkoen.underbot.messages.RequestUsage;

/**
 * Created by Under_Koen on 02/03/2018.
 */
public class Main {
    public static SocketHandler socketHandler;
    public static ModuleHandler moduleHandler;
    public static AssetHandler assetHandler;

    public static void main(String args[]) {
        assetHandler = new AssetHandler();

        if (!assetHandler.hasAssets()) {
            assetHandler.createAssets();
            return;
        }

        socketHandler = new SocketHandler();
        socketHandler.start();

        moduleHandler = new ModuleHandler();

        socketHandler.addMessage(new RequestUsage());
        socketHandler.addMessage(new RequestLog());
        socketHandler.addMessage(new RequestModules(moduleHandler));
        socketHandler.addMessage(new ModuleAction(moduleHandler));

        Runtime.getRuntime().addShutdownHook(new Thread(socketHandler::stop));
    }
}
