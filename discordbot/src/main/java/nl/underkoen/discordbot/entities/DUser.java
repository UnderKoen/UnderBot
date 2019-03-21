package nl.underkoen.discordbot.entities;

import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 30/08/2018.
 */
public class DUser implements nl.underkoen.chatbot.models.User {
    private static List<DUser> users = new ArrayList<>();

    public static DUser getUser(User user) {
        return users.stream()
                .filter(dUser -> dUser.getUser().equals(user))
                .findFirst()
                .orElseGet(() -> {
                    DUser dUser = new DUser(user);
                    users.add(dUser);
                    return dUser;
                });
    }

    private User user;

    private DUser(User user) {
        this.user = user;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    public User getUser() {
        return user;
    }
}
