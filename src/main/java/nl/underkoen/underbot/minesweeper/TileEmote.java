package nl.underkoen.underbot.minesweeper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.underkoen.underbot.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by Under_Koen on 21-07-17.
 */
public class TileEmote {
    public static void createFile() throws Exception {
        File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        file = new File(file.getParent() + "/TileEmotesMinesweeper.json");
        if (file.exists()) return;
        file.createNewFile();
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(getTileEmotesResource());
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTileEmotesResource() {

        StringBuilder result = new StringBuilder("");

        //Get file from resources folder
        ClassLoader classLoader = TileEmote.class.getClassLoader();
        InputStream file = classLoader.getResourceAsStream("TileEmotesMinesweeper.json");

        try (Scanner scanner = new Scanner(file, "UTF-8")) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }

            scanner.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toString();

    }

    public static String getTileEmotes() {

        StringBuilder result = new StringBuilder("");

        try {
            File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            file = new File(file.getParent() + "/TileEmotesMinesweeper.json");

            try (Scanner scanner = new Scanner(file, "UTF-8")) {

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    result.append(line).append("\n");
                }

                scanner.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {}

        return result.toString();
    }

    public static String getTileEmote(TileType type) {
        JsonParser parser = new JsonParser();
        JsonObject o = parser.parse(getTileEmotes()).getAsJsonObject();
        return o.get(type.name()).getAsString();
    }
}
