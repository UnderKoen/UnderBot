package nl.underkoen.underbot.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Created by Under_Koen on 02/03/2018.
 */
public class FileUtil {
    public static File getRunningDir() {
        try {
            return new File(FileUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static String getAllContent(InputStream inputStream) {
        StringBuilder result = new StringBuilder("");

        try (Scanner scanner = new Scanner(inputStream, "UTF-8")) {

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
}
