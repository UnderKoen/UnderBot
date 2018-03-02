package nl.underkoen.underbot.utils;

import com.google.gson.JsonObject;
import nl.underkoen.underbot.models.Usage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;

/**
 * Created by Under_Koen on 11/01/2018.
 */
public class MessageBuilder {
    public static String getUsageMessage(Usage usage) {
        JsonObject json = new JsonObject();
        json.addProperty("method", "usage");
        JsonObject params = new JsonObject();
        params.addProperty("totalCpu", usage.getTotalCpuUsage());
        params.addProperty("processCpu", usage.getProcessCpuUsage());
        params.addProperty("assignedRam", usage.getTotalAssignedRam());
        params.addProperty("processRam", usage.getProcessRamUsage());
        json.add("params", params);
        return json.toString();
    }

    public enum ConsoleType {
        LOG("info"), ERR("error");

        public String name;

        ConsoleType(String name) {
            this.name = name;
        }
    }

    public static String getConsoleMessage(String text, ConsoleType type) {
        JsonObject json = new JsonObject();
        json.addProperty("method", "consoleMessage");
        JsonObject params = new JsonObject();
        params.addProperty("text", text);
        params.addProperty("type", type.name);
        json.add("params", params);
        return json.toString();
    }

    public static String getLogMessage(File log) throws FileNotFoundException, URISyntaxException {
        JsonObject json = new JsonObject();
        json.addProperty("method", "logMessage");
        JsonObject params = new JsonObject();
        params.addProperty("log", FileUtilOld.getFileInput(log));
        json.add("params", params);
        return json.toString();
    }
}
