package nl.underkoen.twitchbot;

import nl.underkoen.chatbot.CommandHandler;
import nl.underkoen.chatbot.models.Command;
import nl.underkoen.twitchbot.chatbot.*;

import java.util.Arrays;

/**
 * Created by Under_Koen on 01/09/2018.
 */
public class TCommandHandler extends CommandHandler<TChannel, TContext, TMember, TMessage, TServer, TUser> {
    @Override
    public boolean canRun(TContext context) {
        return true;
    }

    @Override
    public TContext toContext(Command command, TMessage message) {
        String[] args = message.getContent().split(" ");
        args = Arrays.copyOfRange(args, 1, args.length);
        return new TContext(command, message, args);
    }
}