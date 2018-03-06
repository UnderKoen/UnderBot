package nl.underkoen.underbot.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.models.Module;
import nl.underkoen.underbot.models.Usage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Map;

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
        params.addProperty("log", Main.assetHandler.fileUtil.getContent(log));
        json.add("params", params);
        return json.toString();
    }

    public static String getModulesMessage(Map<Long, Module> modules) {
        JsonObject json = new JsonObject();
        json.addProperty("method", "modulesMessage");
        JsonObject params = new JsonObject();
        JsonArray modulesJ = new JsonArray();
        modules.forEach((aLong, module) -> {
            Boolean canStart = false;
            Boolean canStop = false;
            Boolean canRestart = false;
            switch (module.getStatus()) {
                case NONE:
                    canStart = true;
                    break;
                case INITIALIZING:
                    break;
                case RUNNING:
                    canStop = true;
                    canRestart = true;
                    break;
                case STOPPED:
                    canStart = true;
                    break;
                case CRASHED:
                    canStart = true;
                    break;
            }
            JsonObject moduleJ = new JsonObject();
            moduleJ.addProperty("name", module.getModuleInfo().getName());
            moduleJ.addProperty("id", aLong.toString());
            moduleJ.addProperty("status", module.getStatus().name());
            moduleJ.addProperty("upSince", module.getUpSince());
            moduleJ.addProperty("canStart", canStart);
            moduleJ.addProperty("canStop", canStop);
            moduleJ.addProperty("canRestart", canRestart);
            modulesJ.add(moduleJ);
        });
        params.add("modules", modulesJ);
        json.add("params", params);
        return json.toString();
    }

    public static String getLoginMessage(boolean succes) {
        JsonObject json = new JsonObject();
        json.addProperty("method", "login");
        JsonObject params = new JsonObject();
        params.addProperty("succes", succes);
        json.add("params", params);
        return json.toString();
    }
}
