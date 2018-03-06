package nl.underkoen.underbot.handlers;

import nl.underkoen.underbot.Main;
import nl.underkoen.underbot.utils.FileUtil;
import nl.underkoen.underbot.utils.ModuleFileUtil;

import java.io.File;

/**
 * Created by Under_Koen on 05/03/2018.
 */
public class AssetHandler {
    public ModuleFileUtil fileUtil;

    public AssetHandler() {
        fileUtil = new ModuleFileUtil(null, Main.class.getClassLoader());
    }

    public void createAssets() {
        try {
            File runningDir = FileUtil.getRunningDir();
            File moduleDir = new File(runningDir.getPath() + "/modules");
            moduleDir.mkdir();

            fileUtil.copyResourceToPersonalDir("config.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean hasAssets() {
        File runningDir = FileUtil.getRunningDir();
        File moduleDir = new File(runningDir.getPath() + "/modules");
        File configFile = new File(runningDir.getPath() + "/config.json");
        if (!moduleDir.exists() || !configFile.exists()) return false;
        return true;
    }
}
