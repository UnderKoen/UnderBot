package nl.underkoen.discordbot.minesweeper;

import com.google.gson.JsonParser;
import nl.underkoen.discordbot.Main;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class TileEmote {
    public static void createFile() throws Exception {
        Main.main.getModuleFileUtil().copyResourceToPersonalDir("TileEmotesMinesweeper.json");
    }

    static String getTileEmote(TileType type) {
        JsonParser parser = new JsonParser();
        return parser.parse(Main.main.getModuleFileUtil().getContent("TileEmotesMinesweeper.json")).getAsJsonObject().get(type.name()).getAsString();
    }
}
