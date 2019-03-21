package nl.underkoen.discordbot.minesweeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class Map {
    private ArrayList<Location> bombs;

    private java.util.Map<Location, TileType> completedMap;

    private final int AMOUNT_OF_BOMBS = 10;

    public Map() {
        List<Location> map = new ArrayList<>();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Location loc = new Location(x, y);
                map.add(loc);
            }
        }

        completedMap = new HashMap<>();
        bombs = new ArrayList<>();
        for (int i = 0; i < AMOUNT_OF_BOMBS; i++) {
            int random = new Random().nextInt(81);
            Location loc = (Location) map.toArray()[random];
            while (completedMap.get(loc) == TileType.BOMB) {
                random = new Random().nextInt(81);
                loc = (Location) map.toArray()[random];
            }
            bombs.add(loc);
            completedMap.put(loc, TileType.BOMB);
        }

        for (Location loc : map) {
            if (completedMap.get(getLocationFromCompletedMap(loc.getX(), loc.getY())) == TileType.BOMB) continue;
            int bombs = 0;
            for (Location loc2 : getTilesAroundTile(loc)) {
                if (completedMap.get(loc2) == TileType.BOMB) {
                    bombs++;
                }
            }
            TileType type = null;
            switch (bombs) {
                case 0:
                    type = TileType.NOTHING;
                    break;
                case 1:
                    type = TileType.ONE;
                    break;
                case 2:
                    type = TileType.TWO;
                    break;
                case 3:
                    type = TileType.THREE;
                    break;
                case 4:
                    type = TileType.FOUR;
                    break;
                case 5:
                    type = TileType.FIVE;
                    break;
                case 6:
                    type = TileType.SIX;
                    break;
                case 7:
                    type = TileType.SEVEN;
                    break;
                case 8:
                    type = TileType.EIGHT;
                    break;
            }
            completedMap.put(loc, type);
        }
    }

    private List<Location> getTilesAroundTile(Location loc) {
        ArrayList<Location> tiles = new ArrayList<>();
        int x = loc.getX();
        int y = loc.getY();
        for (int y2 = y - 1; y2 <= y + 1; y2++) {
            for (int x2 = x - 1; x2 <= x + 1; x2++) {
                Location loc2 = getLocationFromCompletedMap(x2, y2);
                tiles.add(loc2);
            }
        }
        return tiles;
    }
    private Location getLocationFromCompletedMap(int x, int y) {
        for (Location loc : completedMap.keySet()) {
            if (loc.getX() == x && loc.getY() == y) return loc;
        }
        return null;
    }

    public String toMessage() {
        StringBuilder string = new StringBuilder();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Location loc = getLocationFromCompletedMap(x, y);
                string.append("||");
                string.append(TileEmote.getTileEmote(completedMap.get(loc)));
                string.append("||");
            }
            string.append("\n");
        }
        return string.toString();
    }
}
