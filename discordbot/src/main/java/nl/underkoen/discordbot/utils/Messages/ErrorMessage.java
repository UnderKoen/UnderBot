package nl.underkoen.discordbot.utils.Messages;

import nl.underkoen.discordbot.entities.DMember;
import sx.blah.discord.handle.impl.obj.Embed;
import sx.blah.discord.handle.obj.IEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 19-04-17.
 */
public class ErrorMessage extends UnderMessage {
    private Color color = Color.RED;

    private DMember user;

    private List<IEmbed.IEmbedField> fields;

    public ErrorMessage(DMember user, String error) {
        this.user = user;
        fields = new ArrayList();
        fields.add(new Embed.EmbedField("Error:", error, true));
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
    public List<IEmbed.IEmbedField> getFields() {
        return fields;
    }
}
