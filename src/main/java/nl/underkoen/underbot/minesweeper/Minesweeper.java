package nl.underkoen.underbot.minesweeper;

import nl.underkoen.underbot.entities.Member;
import nl.underkoen.underbot.utils.Messages.TextMessage;
import sx.blah.discord.handle.obj.IChannel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class Minesweeper {
    public static List<Minesweeper> games = new ArrayList<>();

    Member owner;
    Map map;

    public Minesweeper(Member owner) {
        this.owner = owner;
        map = new Map();
        games.remove(getGame(owner));
        games.add(this);
    }

    public static Minesweeper getGame(Member owner) {
        for (Minesweeper game: games) {
            if (game.getOwner() == owner) return game;
        }
        return null;
    }

    public Map getMap() {
        return map;
    }

    public Member getOwner() {

        return owner;
    }

    public void sendMap(IChannel channel) {
        new TextMessage().setTitle("MineSweeper").addText(map.toMessage()).setMention(owner).sendMessage(channel);
    }
}