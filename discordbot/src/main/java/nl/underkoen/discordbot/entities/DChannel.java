package nl.underkoen.discordbot.entities;

import nl.underkoen.chatbot.models.Channel;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 02/09/2018.
 */
public class DChannel implements Channel<DServer> {
    private static List<DChannel> channels = new ArrayList<>();

    public static DChannel getChannel(DServer server, long id) {
        return getChannel(server.getGuild().getChannelByID(id));
    }

    public static DChannel getChannel(IChannel channel) {
        return channels.stream()
                .filter(dChannel -> dChannel.getChannel().equals(channel))
                .findFirst()
                .orElseGet(() -> {
                    DChannel dChannel = new DChannel(channel);
                    channels.add(dChannel);
                    return dChannel;
                });
    }

    IChannel channel;
    DServer server;

    private DChannel(IChannel channel) {
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

    public IChannel getChannel() {
        return channel;
    }
}