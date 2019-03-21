package nl.underkoen.chatbot;

import nl.underkoen.chatbot.models.Command;
import nl.underkoen.chatbot.utils.ClassFinder;

import java.util.Objects;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public class CommandLoader {
    private CommandLoader() {
    }

    public static void loadCommands(String packageName, CommandHandler handler) {
        ClassFinder.getClassesIn(packageName).stream()
                .filter(CommandLoader::hasCommandSuper)
                .map(CommandLoader::getInstance)
                .filter(Objects::nonNull)
                .forEach(handler::registerCommand);
    }

    private static Command getInstance(Class cls) {
        try {
            return (Command) cls.newInstance();
        } catch (Exception ignored) {
            return null;
        }
    }

    private static boolean hasCommandSuper(Class cls) {
        if (cls.isInterface()) return false;
        return Command.class.isAssignableFrom(cls);
    }
}
