package nl.underkoen.underbot.utils;

import java.awt.*;

/**
 * Created by Under_Koen on 21-04-17.
 */
public class ColorUtil {
    public static Color hexToColor(String colorStr) {
        colorStr = colorStr.replace("#","");
        return new Color(
                Integer.valueOf(colorStr.substring(0, 2), 16),
                Integer.valueOf(colorStr.substring(2, 4), 16),
                Integer.valueOf(colorStr.substring(4, 6), 16));
    }
}
