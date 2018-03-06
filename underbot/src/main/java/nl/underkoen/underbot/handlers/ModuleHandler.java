package nl.underkoen.underbot.handlers;

import com.google.gson.Gson;
import lombok.Getter;
import nl.underkoen.underbot.models.Module;
import nl.underkoen.underbot.models.ModuleInfo;
import nl.underkoen.underbot.models.Status;
import nl.underkoen.underbot.utils.FileUtil;
import nl.underkoen.underbot.utils.Logger;
import nl.underkoen.underbot.utils.ModuleFileUtil;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by Under_Koen on 02/03/2018.
 */
public class ModuleHandler {
    @Getter
    private Map<Long, Module> modules;

    public ModuleHandler() {
        modules = getAllModules();
        initAll();
        startAll();
    }

    private Map<Long, Module> getAllModules() {
        File runningDir = FileUtil.getRunningDir();
        File moduleDir = new File(runningDir.getPath() + "/modules");
        Map<Long, Module> modules = new HashMap<>();
        for (File moduleFile : moduleDir.listFiles()) {
            try {
                if (!moduleFile.getName().endsWith(".jar")) continue;
                URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{moduleFile.toURL()});
                String json = FileUtil.getAllContent(urlClassLoader.getResourceAsStream("moduleInfo.json"));
                ModuleInfo info = new Gson().fromJson(json, ModuleInfo.class);
                ModuleFileUtil moduleFileUtil = new ModuleFileUtil(info, urlClassLoader);
                Object obj = urlClassLoader.loadClass(info.getMainClass()).getDeclaredConstructor(ModuleInfo.class, ModuleFileUtil.class).newInstance(info, moduleFileUtil);
                if (obj instanceof Module) {
                    Module module = (Module) obj;
                    Random rnd = new Random();
                    Long id = rnd.nextLong();
                    while (modules.containsValue(id) || id <= 0) {
                        id = rnd.nextLong();
                    }
                    modules.put(id, module);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return modules;
    }

    public void init(Long id) {
        init(modules.get(id));
    }

    public void init(Module module) {
        try {
            Logger.log("Initializing: " + module.getModuleInfo().getName());
            if (!module.init()) stop(module);
        } catch (Exception e) {
            Logger.log(module.getModuleInfo().getName() + " crashed.");
            e.printStackTrace();
            try {
                module.onCrash();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    public void initAll() {
        //TODO sort on depencies when added
        for (Module module : modules.values()) {
            init(module);
        }
    }

    public void start(Long id) {
        start(modules.get(id));
    }

    public void start(Module module) {
        try {
            if (module.getStatus() == Status.CRASHED) return;
            Logger.log("Starting: " + module.getModuleInfo().getName());
            if (!module.start()) stop(module);
        } catch (Exception e) {
            Logger.log(module.getModuleInfo().getName() + " crashed.");
            e.printStackTrace();
            try {
                module.onCrash();
            } catch (Exception e1) {
                e1.printStackTrace();

            }
        }
    }

    public void startAll() {
        //TODO sort on depencies when added
        for (Module module : modules.values()) {
            start(module);
        }
    }

    public void stop(Long id) {
        stop(modules.get(id));
    }

    public void stop(Module module) {
        try {
            if (module.getStatus() == Status.CRASHED) return;
            Logger.log("Stopping: " + module.getModuleInfo().getName());
            module.stop();
        } catch (Exception e) {
            Logger.log(module.getModuleInfo().getName() + " crashed.");
            e.printStackTrace();
            try {
                module.onCrash();
            } catch (Exception e1) {
                e1.printStackTrace();

            }
        }
    }

    public void stopAll() {
        for (Module module : modules.values()) {
            stop(module);
        }
    }
}
