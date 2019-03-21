package nl.underkoen.discordbot.entities;

import net.dv8tion.jda.core.entities.TextChannel;
import nl.underkoen.chatbot.models.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 02/09/2018.
 */
public class DChannel implements Channel<DServer> {
    private static List<DChannel> channels = new ArrayList<>();

    public static DChannel getChannel(DServer server, long id) {
        return getChannel(server.getGuild().getTextChannelById(id));
    }

    public static DChannel getChannel(TextChannel channel) {
        return channels.stream()
                .filter(dChannel -> dChannel.getChannel().equals(channel))
                .findFirst()
                .orElseGet(() -> {
                    DChannel dChannel = new DChannel(channel);
                    channels.add(dChannel);
                    return dChannel;
                });
    }

    private TextChannel channel;
    private DServer server;

    private DChannel(TextChannel channel) {
        this.channel = channel;
        this.server = DServer.getServer(channel.getGuild());
    }

    @Override
    public String getName() {
        return channel.getName();
    }

    @Override
    public void sendMessage(String message) {
        channel.sendMessage(message);
    }

    @Override
    public DServer getServer() {
        return server;
    }

    public TextChannel getChannel() {
        return channel;
    }
}