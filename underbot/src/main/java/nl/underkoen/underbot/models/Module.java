package nl.underkoen.underbot.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Under_Koen on 02/03/2018.
 */
@AllArgsConstructor
public abstract class Module {
    @Getter
    private ModuleInfo moduleInfo;

    /**
     * Is called before start()
     */
    public abstract void init();

    /**
     * Is called after every module has initialized
     */
    public abstract void start();

    /**
     * Is called when the programs stops or when specified this module needs to shut down
     */
    public abstract void stop();

    public abstract String getStatus();
}
