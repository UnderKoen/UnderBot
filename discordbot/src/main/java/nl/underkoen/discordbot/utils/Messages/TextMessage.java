package nl.underkoen.discordbot.utils.Messages;

import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.utils.ColorUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.dv8tion.jda.core.entities.MessageEmbed.Field;

/**
 * Created by Under_Koen on 21-04-17.
 */
public class TextMessage extends UnderMessage {
    private Color color = ColorUtil.hexToColor("#2a6886");

    private DMember user;

    private String message;

    private String title;

    private String url;

    private List<Field> fields;

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
        fields.add(new Field(name, value, inline));
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
    public List<Field> getFields() {
        return fields;
    }
}
