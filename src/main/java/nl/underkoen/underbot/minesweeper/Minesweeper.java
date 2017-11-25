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
    private static List<Minesweeper> games = new ArrayList<>();

    private Member owner;
    private Map map;

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
        return new Minesweeper(owner);
    }

    public Map getMap() {
        return map;
    }

    private Member getOwner() {
        return owner;
    }

    public void sendMap(IChannel channel) {
        new TextMessage().setTitle("MineSweeper").addText(map.toMessage()).setMention(owner).sendMessage(channel);
    }
}