package nl.underkoen.discordbot.minesweeper;

import nl.underkoen.discordbot.entities.DChannel;
import nl.underkoen.discordbot.entities.DMember;
import nl.underkoen.discordbot.utils.Messages.TextMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class Minesweeper {
    private static List<Minesweeper> games = new ArrayList<>();

    private DMember owner;
    private Map map;

    public Minesweeper(DMember owner) {
        this.owner = owner;
        map = new Map();
        games.remove(getGame(owner));
        games.add(this);
    }

    public static Minesweeper getGame(DMember owner) {
        for (Minesweeper game : games) {
            if (game.getOwner() == owner) return game;
        }
        return null;
    }

    public Map getMap() {
        return map;
    }

    private DMember getOwner() {
        return owner;
    }

    public void sendMap(DChannel channel) {
        new TextMessage().setTitle("MineSweeper").addText(map.toMessage()).setMention(owner).sendMessage(channel);
    }
}