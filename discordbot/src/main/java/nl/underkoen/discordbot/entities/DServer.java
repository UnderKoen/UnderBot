package nl.underkoen.discordbot.entities;

import net.dv8tion.jda.core.entities.Guild;
import nl.underkoen.chatbot.models.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public class DServer implements Server<DChannel, DMember, DUser> {
    private static List<DServer> servers = new ArrayList<>();

    public static DServer getServer(Guild guild) {
        return servers.stream()
                .filter(server -> server.getGuild().equals(guild))
                .findFirst()
                .orElseGet(() -> {
                    DServer server = new DServer(guild);
                    servers.add(server);
                    return server;
                });
    }

    private Guild guild;

    private DServer(Guild guild) {
        this.guild = guild;
    }

    @Override
    public String getName() {
        return guild.getName();
    }

    @Override
    public List<DChannel> getChannels() {
        return guild.getTextChannels().stream()
                .map(DChannel::getChannel)
                .collect(Collectors.toList());
    }

    @Override
    public DMember getMember(DUser user) {
        return DMember.getMember(this, user);
    }

    public Guild getGuild() {
        return guild;
    }
}