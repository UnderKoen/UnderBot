package nl.underkoen.underbot.utils;

import java.time.LocalTime;

/**
 * Created by Under_Koen on 05/03/2018.
 */
public class Logger {
    public static void log(String message) {
        System.out.println(LocalTime.now() + ": " + message);
    }
}
