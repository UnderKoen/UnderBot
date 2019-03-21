package nl.underkoen.discordbot.utils.Messages;

import nl.underkoen.discordbot.entities.DMember;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.dv8tion.jda.core.entities.MessageEmbed.Field;

/**
 * Created by Under_Koen on 19-04-17.
 */
public class ErrorMessage extends UnderMessage {
    private Color color = Color.RED;

    private DMember user;

    private List<Field> fields;

    public ErrorMessage(DMember user, String error) {
        this.user = user;
        fields = new ArrayList<>();
        fields.add(new Field("Error:", error, true));
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
    public List<Field> getFields() {
        return fields;
    }
}
