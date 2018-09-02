package nl.underkoen.discordbot.entities;

import nl.underkoen.chatbot.models.User;
import sx.blah.discord.handle.obj.IUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public class DUser implements User {
    private static List<DUser> users = new ArrayList<>();

    public static DUser getUser(IUser user) {
        return users.stream()
                .filter(dUser -> dUser.getUser().equals(user))
                .findFirst()
                .orElseGet(() -> {
                    DUser dUser = new DUser(user);
                    users.add(dUser);
                    return dUser;
                });
    }

    private IUser user;

    private DUser(IUser user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    public IUser getUser() {
        return user;
    }
}
