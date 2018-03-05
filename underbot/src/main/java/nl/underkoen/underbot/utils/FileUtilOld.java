package nl.underkoen.underbot.utils;

import nl.underkoen.underbot.Main;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Scanner;

/**
 * Created by Under_Koen on 24/11/2017.
 */
public class FileUtilOld {

    public static void makeDuplicate(String fileName) throws IOException, URISyntaxException {
        makeDuplicate(fileName, fileName);
    }

    public static void makeDuplicate(String input, String output) throws IOException, URISyntaxException {
        File mainFile = getFile("/");
        File file = getFile(output);
        if (file.exists()) return;
        String[] dirs = output.split("/");
        for (int i = 0; i < dirs.length - 1; i++) {
            String dir = dirs[i];
            new File(mainFile + "/" + dir).mkdir();
        }
        file.createNewFile();
        updateFile(file, getResource(input));
    }

    public static void updateFile(String fileName, String text) throws URISyntaxException, IOException {
        updateFile(getFile(fileName), text);
    }

    public static void updateFile(File file, String text) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(text);
        fileWriter.flush();
        fileWriter.close();
    }

    public static File getFile(String fileName) throws URISyntaxException {
        if (fileName.charAt(0) == '/') {
            fileName = fileName.replaceFirst("/", "");
        }
        File mainFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        File file = new File(mainFile.getParent() + "/" + fileName);
        return file;
    }

    public static String getFileInput(String fileName) throws FileNotFoundException, URISyntaxException {
        return scanFile(new FileInputStream(getFile(fileName)));
    }

    public static String getFileInput(File file) throws FileNotFoundException, URISyntaxException {
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
