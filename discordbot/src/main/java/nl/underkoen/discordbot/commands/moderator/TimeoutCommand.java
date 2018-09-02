package nl.underkoen.discordbot.commands.moderator;

import nl.underkoen.chatbot.models.RankAccessible;
import nl.underkoen.discordbot.DiscordBot;
import nl.underkoen.discordbot.Roles;
import nl.underkoen.discordbot.entities.DCommand;
import nl.underkoen.discordbot.entities.DContext;
import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.entities.DServer;
import nl.underkoen.discordbot.utils.ColorUtil;
import nl.underkoen.discordbot.utils.Messages.ErrorMessage;
import nl.underkoen.discordbot.utils.Messages.TextMessage;
import sx.blah.discord.handle.impl.events.guild.category.CategoryCreateEvent;
import sx.blah.discord.handle.impl.events.guild.channel.ChannelCreateEvent;
import sx.blah.discord.handle.impl.events.guild.voice.VoiceChannelCreateEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.PermissionUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Under_Koen on 04-05-17.
 */
public class TimeoutCommand implements DCommand, RankAccessible {
    private String command = "timeout";
    private String usage = "/timeout [User] [Lenght] (reason...)";
    private String description = "Time out a user so he can't talk.";
    private int minimumRole = Roles.MOD.role;

    private HashMap<DServer, IRole> timeoutRoles = new HashMap<>();
    private ExecutorService timeouts;

    @Override
    public int getMinimumRank() {
        return minimumRole;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public String getUsage() {
        return usage;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setup() {
        timeouts = Executors.newCachedThreadPool();
        for (IGuild guild : DiscordBot.client.getGuilds()) {
            IRole timeoutRole;
            if (guild.getRolesByName("Muted").isEmpty()) {
                timeoutRole = guild.createRole();
                timeoutRole.changeName("Muted");
                timeoutRole.changeColor(ColorUtil.hexToColor("#790606"));
            } else {
                timeoutRole = guild.getRolesByName("Muted").get(0);
            }
            for (IChannel textChannel : guild.getChannels()) {
                if (!canEdit(textChannel)) continue;
                textChannel.overrideRolePermissions(timeoutRole, null, EnumSet.of(Permissions.SEND_MESSAGES,
                        Permissions.ADD_REACTIONS));
            }
            for (IVoiceChannel voiceChannel : guild.getVoiceChannels()) {
                if (!canEdit(voiceChannel)) continue;
                voiceChannel.overrideRolePermissions(timeoutRole, null, EnumSet.of(Permissions.VOICE_SPEAK));
            }
            for (ICategory category : guild.getCategories()) {
                category.overrideRolePermissions(timeoutRole, null, EnumSet.of(Permissions.SEND_MESSAGES,
                        Permissions.ADD_REACTIONS, Permissions.VOICE_SPEAK));
            }
            timeoutRoles.put(DServer.getServer(guild), timeoutRole);
        }
        DiscordBot.client.getDispatcher().registerListener(event -> {
            if (event instanceof ChannelCreateEvent) {
                ChannelCreateEvent channelCreateEvent = (ChannelCreateEvent) event;
                IRole timeoutRole = timeoutRoles.get(channelCreateEvent.getGuild());
                channelCreateEvent.getChannel().overrideRolePermissions(timeoutRole, null, EnumSet.of(Permissions.SEND_MESSAGES,
                        Permissions.ADD_REACTIONS));
            } else if (event instanceof VoiceChannelCreateEvent) {
                VoiceChannelCreateEvent voiceChannelCreateEvent = (VoiceChannelCreateEvent) event;
                IRole timeoutRole = timeoutRoles.get(voiceChannelCreateEvent.getGuild());
                voiceChannelCreateEvent.getVoiceChannel().overrideRolePermissions(timeoutRole, null,
                        EnumSet.of(Permissions.VOICE_SPEAK));
            }
            if (event instanceof CategoryCreateEvent) {
                CategoryCreateEvent categoryCreateEvent = (CategoryCreateEvent) event;
                IRole timeoutRole = timeoutRoles.get(categoryCreateEvent.getGuild());
                categoryCreateEvent.getCategory().overrideRolePermissions(timeoutRole, null, EnumSet.of(Permissions.SEND_MESSAGES,
                        Permissions.ADD_REACTIONS, Permissions.VOICE_SPEAK));
            }
        });
    }

    private boolean canEdit(IChannel channel) {
        return PermissionUtils.hasPermissions(channel, DiscordBot.client.getOurUser(), Permissions.MANAGE_PERMISSIONS);
    }

    @Override
    public void trigger(DContext contxt) {
        DContext context = contxt;
        if (context.getRawArgs().length == 0 || context.getRawArgs().length < 2) {
            new ErrorMessage(context.getMember(), ("This command needs " + ((context.getRawArgs().length == 0) ? "" : "more") + " arguments to work"))
                    .sendMessage(context.getChannel());
            return;
        }
        TextMessage message = new TextMessage();
        message.setMention(context.getMember());
        Pattern pattern = Pattern.compile("<@!?(\\d+)>");
        Matcher matcher = pattern.matcher(context.getRawArgs()[0]);
        matcher.find();
        DMember member = null;
        try {
            member = DMember.getMember(context.getServer(), context.getServer().getGuild()
                    .getUserByID(Long.parseLong(matcher.group(1))));
        } catch (Exception e) {
            new ErrorMessage(context.getMember(), context.getRawArgs()[0] + " is not a valid user.")
                    .sendMessage(context.getChannel());
            return;
        }
        pattern = Pattern.compile("(\\d+)([smhdwMy])");
        matcher = pattern.matcher(context.getRawArgs()[1]);
        LocalDateTime length = new Timestamp(System.currentTimeMillis()).toLocalDateTime();
        while (matcher.find()) {
            TemporalUnit unit = null;
            switch (matcher.group(2)) {
                case "s":
                    unit = ChronoUnit.SECONDS;
                    break;
                case "m":
                    unit = ChronoUnit.MINUTES;
                    break;
                case "h":
                    unit = ChronoUnit.HOURS;
                    break;
                case "d":
                    unit = ChronoUnit.DAYS;
                    break;
                case "w":
                    unit = ChronoUnit.WEEKS;
                    break;
                case "M":
                    unit = ChronoUnit.MONTHS;
                    break;
                case "y":
                    unit = ChronoUnit.YEARS;
                    break;
            }
            length = length.plus(Integer.parseInt(matcher.group(1)), unit);
        }
        if (!matcher.replaceAll("").isEmpty()) {
            new ErrorMessage(context.getMember(), context.getRawArgs()[1] + " is not a valid length.")
                    .sendMessage(context.getChannel());
            return;
        }

        IRole timeoutRole = timeoutRoles.get(member.getServer());
        member.getUser().getUser().addRole(timeoutRole);

        LocalDateTime finalLength = length;
        DMember finalMember = member;
        timeouts.submit(() -> {
            Timestamp until = Timestamp.valueOf(finalLength);
            while (new Timestamp(System.currentTimeMillis()).before(until)) {
                try {
                    timeouts.awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            finalMember.getUser().getUser().removeRole(timeoutRole);
        });

        TextMessage msg = new TextMessage().addText("Timeout information")
                .setMention(context.getMember())
                .addField("User", member.getAsMention(), false)
                .addField("Until", Timestamp.valueOf(length).toLocaleString(), false);
        if (context.getRawArgs().length >= 3) {
            StringBuilder str = new StringBuilder();
            int i = 1;
            for (String arg : context.getRawArgs()) {
                if (i <= 2) {
                    i++;
                    continue;
                }
                str.append(arg).append(" ");
                i++;
            }
            msg.addField("Reason", str.toString(), false);
        }
        msg.sendMessage(context.getChannel());
    }
}
