package nl.underkoen.underbot.utils;

import nl.underkoen.underbot.Main;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Created by Under_Koen on 24/11/2017.
 */
public class FileUtil {

    public static void makeDuplicate(String fileName) throws IOException, URISyntaxException {
        makeDuplicate(fileName, fileName);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void makeDuplicate(String input, String output) throws IOException, URISyntaxException {
        if (input.charAt(0) == '/') {
            input = input.replaceFirst("/", "");
        }
        if (output.charAt(0) == '/') {
            output = output.replaceFirst("/", "");
        }
        File mainFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParentFile();
        File file = new File(mainFile + "/" + output);
        if (file.exists()) return;
        String[] dirs = output.split("/");
        for (int i = 0; i < dirs.length; i++) {
            String dir = dirs[i];
            if (dirs.length - 1 == i) continue;
            new File(mainFile + "/" + dir).mkdir();
        }
        file.createNewFile();
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(getResource(input));
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateFile(String fileName, String text) throws URISyntaxException, IOException {
        if (fileName.charAt(0) == '/') {
            fileName = fileName.replaceFirst("/", "");
        }
        File file = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        file = new File(file.getParent() + "/" + fileName);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(text);
        fileWriter.flush();
        fileWriter.close();
    }

    public static String getFile(String fileName) throws FileNotFoundException, URISyntaxException {
        if (fileName.charAt(0) == '/') {
            fileName = fileName.replaceFirst("/", "");
        }
        File mainFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        File file = new File(mainFile.getParent() + "/" + fileName);
        return scanFile(new FileInputStream(file));
    }

    private static String getResource(String fileName) {
        if (fileName.charAt(0) == '/') {
            fileName = fileName.replaceFirst("/", "");
        }
        ClassLoader classLoader = Main.class.getClassLoader();
        InputStream file = classLoader.getResourceAsStream(fileName);

        return scanFile(file);

    }

    private static String scanFile(InputStream file) {
        StringBuilder result = new StringBuilder("");

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
}
