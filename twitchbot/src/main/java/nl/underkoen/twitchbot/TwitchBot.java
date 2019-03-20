package nl.underkoen.twitchbot;

import nl.underkoen.chatbot.CommandHandler;
import nl.underkoen.chatbot.commands.SimpleCommand;
import nl.underkoen.twitchbot.chatbot.TwitchHandler;
import nl.underkoen.underbot.models.Module;
import nl.underkoen.underbot.models.ModuleInfo;
import nl.underkoen.underbot.models.Status;
import nl.underkoen.underbot.utils.ModuleFileUtil;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public class TwitchBot extends Module {
    public static CommandHandler commandHandler;
    public static TwitchHandler twitchHandler;

    public static final String PREFIX = "!";

    public static void main(String[] args) {
        ModuleInfo info = new ModuleInfo(null, "Twitch", null);
        TwitchBot module = new TwitchBot(info, new ModuleFileUtil(info, TwitchBot.class.getClassLoader()));
        module.init();
        module.start();
    }

    public TwitchBot(ModuleInfo moduleInfo, ModuleFileUtil moduleFileUtil) {
        super(moduleInfo, moduleFileUtil);
    }

    @Override
    public boolean init() {
        commandHandler = new TCommandHandler();
        twitchHandler = new TwitchHandler(commandHandler);

        commandHandler.registerCommand(new SimpleCommand(PREFIX, "test", "", () -> "TESTING 123"));
        commandHandler.registerCommand(new SimpleCommand(PREFIX, "discor", "", () -> "WOWOWOWO nee..."));

        twitchHandler.init("underbot1337", "oauth:ffzg5af1u03f62plkazvz9wgelpnyl");
        twitchHandler.setOnConnect(() -> {
            twitchHandler.joinServer("#makertim");
        });
        return true;
    }

    @Override
    public boolean start() {
        twitchHandler.start();
        return true;
    }

    @Override
    public void stop() {

    }

    @Override
    public void onCrash() {
    }

    @Override
    public Status getStatus() {
        return null;
    }
}
