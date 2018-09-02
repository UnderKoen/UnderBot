package nl.underkoen.discordbot.utils.Messages;

import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.utils.ColorUtil;
import sx.blah.discord.handle.impl.obj.Embed;
import sx.blah.discord.handle.obj.IEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 21-04-17.
 */
public class TextMessage extends UnderMessage {
    private Color color = ColorUtil.hexToColor("#2a6886");

    private DMember user;

    private String message;

    private String title;

    private String url;

    private List<IEmbed.IEmbedField> fields;

    public TextMessage setMention(DMember user) {
        this.user = user;
        return this;
    }

    public TextMessage setUrl(String url) {
        this.url = url;
        return this;
    }

    public TextMessage addText(String text) {
        if (message == null) {
            message = "";
        } else {
            message = message + "\n";
        }
        message = message + text;
        return this;
    }

    public TextMessage addField(String name, String value, boolean inline) {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        if (name == null || name.isEmpty()) {
            name = "\u200E";
        }
        if (value == null || value.isEmpty()) {
            value = "\u200E";
        }
        fields.add(new Embed.EmbedField(name, value, inline));
        return this;
    }

    public TextMessage setTitle(String title) {
        this.title = title;
        return this;
    }

    public TextMessage setTitle(String title, String url) {
        this.title = title;
        this.url = url;
        return this;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public DMember getAuthor() {
        return user;
    }

    @Override
    public String getDescription() {
        return message;
    }

    @Override
    public List<IEmbed.IEmbedField> getFields() {
        return fields;
    }
}
