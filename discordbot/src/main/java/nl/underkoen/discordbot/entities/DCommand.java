package nl.underkoen.discordbot.entities;

import nl.underkoen.chatbot.models.Command;
import nl.underkoen.discordbot.DiscordBot;

/**
 * Created by Under_Koen on 19-04-17.
 */
public interface DCommand extends Command<DContext> {
    @Override
    default String getPrefix() {
        return DiscordBot.PREFIX;
    }
}
