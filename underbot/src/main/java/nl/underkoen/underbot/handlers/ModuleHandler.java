package nl.underkoen.underbot.handlers;

import com.google.gson.Gson;
import lombok.Getter;
import nl.underkoen.underbot.models.Module;
import nl.underkoen.underbot.models.ModuleInfo;
import nl.underkoen.underbot.utils.FileUtil;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Under_Koen on 02/03/2018.
 */
public class ModuleHandler {
    @Getter
    private static List<Module> modules = new ArrayList<>();

    public ModuleHandler() {
        if (hasAssets()) {
            try {
                initAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            createAssets();
        }
    }

    public void initAll() throws Exception {
        File runningDir = FileUtil.getRunningDir();
        File moduleDir = new File(runningDir.getPath() + "/modules");
        for (File moduleFile : moduleDir.listFiles()) {
            if (moduleFile.getName().equals(".DS_Store")) continue;
            URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{moduleFile.toURL()});
            String json = FileUtil.getAllContent(urlClassLoader.getResourceAsStream("moduleInfo.json"));
            ModuleInfo info = new Gson().fromJson(json, ModuleInfo.class);
            Object obj = urlClassLoader.loadClass(info.mainClass).getDeclaredConstructor(ModuleInfo.class).newInstance(info);
            if (obj instanceof Module) {
                Module module = (Module) obj;
                module.init();
                modules.add(module);
            }
        }
        startAll();
    }

    public void startAll() {
        //TODO sort on depencies when i added those
        modules.forEach(module -> {
            module.start();
        });
    }

    public void stopAll() {
        modules.forEach(module -> {
            module.stop();
        });
    }

    public void createAssets() {
        File runningDir = FileUtil.getRunningDir();
        File moduleDir = new File(runningDir.getPath() + "/modules");
        moduleDir.mkdir();
    }

    public boolean hasAssets() {
        File runningDir = FileUtil.getRunningDir();
        File moduleDir = new File(runningDir.getPath() + "/modules");
        if (!moduleDir.exists()) return false;
        return true;
    }
}
