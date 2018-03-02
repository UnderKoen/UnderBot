package nl.underkoen.underbot.utils;

/**
 * Created by Under_Koen on 11/01/2018.
 */
public class NumberUtil {
    public static double round(double i, int dec) {
        return (int) ((i + 5 / Math.pow(10, dec + 1)) * Math.pow(10, dec)) / Math.pow(10, dec);
    }
}
