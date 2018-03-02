package nl.underkoen.discordbot.minesweeper;

import com.google.gson.JsonParser;
import nl.underkoen.underbot.utils.FileUtilOld;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class TileEmote {
    public static void createFile() throws Exception {
        FileUtilOld.makeDuplicate("TileEmotesMinesweeper.json");
    }

    static String getTileEmote(TileType type) {
        JsonParser parser = new JsonParser();
        try {
            return parser.parse(FileUtilOld.getFileInput("TileEmotesMinesweeper.json")).getAsJsonObject().get(type.name()).getAsString();
        } catch (FileNotFoundException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
