package nl.underkoen.underbot.utils.Messages;

import nl.underkoen.underbot.entities.Member;
import sx.blah.discord.handle.impl.obj.Embed;
import sx.blah.discord.handle.obj.IEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 19-04-17.
 */
public class ErrorMessage implements UnderMessage {
    private Color color = Color.RED;

    private Member user;

    private List<IEmbed.IEmbedField> fields;

    public ErrorMessage(Member user, String error) {
        this.user = user;
        fields = new ArrayList();
        fields.add(new Embed.EmbedField("Error:", error, true));
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Member getAuthor() {
        return user;
    }

    @Override
    public List<IEmbed.IEmbedField> getFields() {
        return fields;
    }
}
