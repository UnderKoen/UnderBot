package nl.underkoen.underbot.utils;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import nl.underkoen.underbot.models.ModuleInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;

/**
 * Created by Under_Koen on 06/03/2018.
 */
@AllArgsConstructor
public class ModuleFileUtil {
    private ModuleInfo moduleInfo;
    @NonNull
    private ClassLoader classLoader;

    public InputStream getResource(String name) {
        return classLoader.getResourceAsStream(name);
    }

    public String getResourceContent(String name) {
        return FileUtil.getAllContent(getResource(name));
    }

    public File getPersonalDir() {
        if (moduleInfo == null) return FileUtil.getRunningDir();
        File file = new File(FileUtil.getRunningDir().getPath() + "/" + moduleInfo.getName());
        if (!file.exists()) file.mkdirs();
        return file;
    }

    public File getFileInPersonalDir(String name) {
        return new File(getPersonalDir().getPath() + "/" + name);
    }

    public String getContent(File file) {
        try {
            return FileUtil.getAllContent(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getContent(String name) {
        return getContent(getFileInPersonalDir(name));
    }

    public void updateFile(File file, String content) {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyResourceToPersonalDir(String name, String out) {
        String content = getResourceContent(name);
        updateFile(getFileInPersonalDir(out), content);
    }

    public void copyResourceToPersonalDir(String name) {
        copyResourceToPersonalDir(name, name);
    }
}
