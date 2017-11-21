package nl.underkoen.underbot.minesweeper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class Map {
    private ArrayList<Location> bombs;

    private java.util.Map<Location, TileType> outline;
    private java.util.Map<Location, TileType> map;
    private java.util.Map<Location, TileType> completedMap;
    private java.util.Map<Location, TileType> visibleMap;

    private final int AMOUNT_OF_BOMBS = 10;

    public Map() {
        outline = new HashMap<Location, TileType>();
        for (int y = -2; y < 9; y++) {
            for (int x = -2; x < 9; x++) {
                if (x >= 0 && y >= 0) continue;
                Location loc = new Location(x, y);
                outline.put(loc, getOutlineType(loc));
            }
        }

        map = new HashMap<>();
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                Location loc = new Location(x, y);
                map.put(loc, TileType.NOTYET);
            }
        }
        completedMap = new HashMap<>();
        bombs = new ArrayList<>();
        for (int i = 0; i < AMOUNT_OF_BOMBS; i++) {
            int random = new Random().nextInt(81);
            Location loc = (Location) map.keySet().toArray()[random];
            while (map.get(loc) == TileType.BOMB) {
                random = new Random().nextInt(81);
                loc = (Location) map.keySet().toArray()[random];
            }
            bombs.add(loc);
            completedMap.put(loc, TileType.BOMB);
        }

        for (Location loc : map.keySet()) {
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
        updateMap();
    }

    private void updateMap() {
        visibleMap = new HashMap<Location, TileType>();
        visibleMap.putAll(map);
        visibleMap.putAll(outline);
    }

    public void flagTile(Location loc) {
        map.replace(loc, TileType.FLAG);
        updateMap();
    }

    public void openTile(Location loc) {
        map.replace(loc, completedMap.get(getLocationFromCompletedMap(loc.getX(), loc.getY())));
        if (completedMap.get(getLocationFromCompletedMap(loc.getX(), loc.getY())) == TileType.NOTHING) {
            for (Location loc2 : getTilesAroundTile(loc)) {
                if (completedMap.get(loc2) == TileType.NOTHING &&
                        map.get(getLocationFromVisibleMap(loc2.getX(), loc2.getY())) != TileType.NOTHING) {
                    openTile(loc2);
                }
                if (loc2 != null) {
                    map.replace(loc2, completedMap.get(getLocationFromCompletedMap(loc2.getX(), loc2.getY())));
                }
            }
        }
        updateMap();
    }

    public List<Location> getTilesAroundTile(Location loc) {
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

    public Location getLocationFromOutline(int x, int y) {
        for (Location loc : outline.keySet()) {
            if (loc.getX() == x && loc.getY() == y) return loc;
        }
        return null;
    }

    public Location getLocationFromVisibleMap(int x, int y) {
        if (!(x >= 0 && y >= 0)) return getLocationFromOutline(x, y);
        for (Location loc : map.keySet()) {
            if (loc.getX() == x && loc.getY() == y) return loc;
        }
        return null;
    }

    public Location getLocationFromCompletedMap(int x, int y) {
        for (Location loc : completedMap.keySet()) {
            if (loc.getX() == x && loc.getY() == y) return loc;
        }
        return null;
    }

    public String toMessage() {
        StringBuilder string = new StringBuilder();
        for (int y = -2; y < 9; y++) {
            for (int x = -2; x < 9; x++) {
                Location loc = getLocationFromVisibleMap(x, y);
                string.append(TileEmote.getTileEmote(visibleMap.get(loc)));
            }
            string.append("\n");
        }
        return string.toString();
    }

    private TileType getOutlineType(Location loc) {
        int x = loc.getX();
        int y = loc.getY();
        if (x == -2 && y < 0 || y == -2 && x < 0) {
            return TileType.FILLING;
        }
        if (x == -2 && y >= 0) {
            return TileType.valueOf("Y" + (char) (y + 65));
        }
        if (y == -2 && x >= 0) {
            return TileType.valueOf("X" + (x + 1));
        }
        return TileType.OUTLINE;
    }

    public boolean isFinished() {
        int foundBombs = 0;
        for (Location bombLoc: bombs) {
            if (map.get(getLocationFromVisibleMap(bombLoc.getX(), bombLoc.getY())) == TileType.FLAG) {
                foundBombs++;
            }
        }
        return foundBombs == AMOUNT_OF_BOMBS;
    }
}
