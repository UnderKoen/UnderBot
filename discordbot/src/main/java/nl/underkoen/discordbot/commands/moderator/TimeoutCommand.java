package nl.underkoen.discordbot.commands.moderator;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.channel.category.CategoryCreateEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.managers.GuildController;
import net.dv8tion.jda.core.requests.restaction.RoleAction;
import net.dv8tion.jda.core.utils.PermissionUtil;
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

    private HashMap<DServer, Role> timeoutRoles = new HashMap<>();
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
        for (Guild guild : DiscordBot.client.getGuilds()) {
            Role timeoutRole;
            if (guild.getRolesByName("Muted", false).isEmpty()) {
                RoleAction temp = guild.getController().createRole();
                temp.setName("Muted");
                temp.setColor(ColorUtil.hexToColor("#790606"));
                timeoutRole = temp.complete();
            } else {
                timeoutRole = guild.getRolesByName("Muted", false).get(0);
            }
            for (TextChannel textChannel : guild.getTextChannels()) {
                if (!canEdit(textChannel)) continue;
                if (textChannel.getPermissionOverride(timeoutRole) == null) {
                    textChannel.createPermissionOverride(timeoutRole).complete();
                }
                textChannel.getPermissionOverride(timeoutRole).getManager().deny(Permission.MESSAGE_WRITE,
                        Permission.MESSAGE_ADD_REACTION).complete();
            }
            for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                if (!canEdit(voiceChannel)) continue;
                if (voiceChannel.getPermissionOverride(timeoutRole) == null) {
                    voiceChannel.createPermissionOverride(timeoutRole).complete();
                }
                voiceChannel.getPermissionOverride(timeoutRole).getManager().deny(Permission.VOICE_SPEAK).complete();
            }
            for (Category category : guild.getCategories()) {
                if (!canEdit(category)) continue;
                if (category.getPermissionOverride(timeoutRole) == null) {
                    category.createPermissionOverride(timeoutRole).complete();
                }
                category.getPermissionOverride(timeoutRole).getManager().deny(Permission.MESSAGE_WRITE,
                        Permission.MESSAGE_ADD_REACTION,
                        Permission.VOICE_SPEAK).complete();
            }
            timeoutRoles.put(DServer.getServer(guild), timeoutRole);
        }
        DiscordBot.client.addEventListener(new ListenerAdapter() {
            @Override
            public void onTextChannelCreate(TextChannelCreateEvent event) {
                Role timeoutRole = timeoutRoles.get(event.getGuild());
                if (event.getChannel().getPermissionOverride(timeoutRole) == null) {
                    event.getChannel().createPermissionOverride(timeoutRole).complete();
                }
                event.getChannel().getPermissionOverride(timeoutRole).getManager().deny(Permission.MESSAGE_WRITE,
                        Permission.MESSAGE_ADD_REACTION).complete();
            }

            @Override
            public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {
                Role timeoutRole = timeoutRoles.get(event.getGuild());
                if (event.getChannel().getPermissionOverride(timeoutRole) == null) {
                    event.getChannel().createPermissionOverride(timeoutRole).complete();
                }
                event.getChannel().getPermissionOverride(timeoutRole).getManager().deny(Permission.VOICE_SPEAK).complete();
            }

            @Override
            public void onCategoryCreate(CategoryCreateEvent event) {
                Role timeoutRole = timeoutRoles.get(event.getGuild());
                if (event.getCategory().getPermissionOverride(timeoutRole) == null) {
                    event.getCategory().createPermissionOverride(timeoutRole).complete();
                }
                event.getCategory().getPermissionOverride(timeoutRole).getManager().deny(Permission.VOICE_SPEAK).complete();
            }
        });
    }

    private boolean canEdit(Channel channel) {
        return PermissionUtil.checkPermission(channel, channel.getGuild().getMember(DiscordBot.client.getSelfUser()), Permission.MANAGE_PERMISSIONS);
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
            Long id = Long.parseLong(matcher.group(1));
            member = DMember.getMember(context.getServer().getGuild().getMemberById(id));
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

        Role timeoutRole = timeoutRoles.get(member.getServer());
        GuildController guildController = new GuildController(member.getServer().getGuild());
        guildController.addSingleRoleToMember(member.getMember(), timeoutRole).complete();

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
            guildController.removeSingleRoleFromMember(finalMember.getMember(), timeoutRole).complete();
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
