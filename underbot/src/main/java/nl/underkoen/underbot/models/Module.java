package nl.underkoen.underbot.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import nl.underkoen.underbot.utils.ModuleFileUtil;

/**
 * Created by Under_Koen on 02/03/2018.
 */
@RequiredArgsConstructor
public abstract class Module {
    @Getter
    @NonNull
    private final ModuleInfo moduleInfo;

    @Getter
    @NonNull
    private final ModuleFileUtil moduleFileUtil;

    @Getter
    private Long upSince;

    /**
     * Is called when the program starts or when the module start at a later moment.
     *
     * @return <code>true</code> when it was successfully initialized.
     * @throws Exception when somethings goes wrong.
     */
    public abstract boolean init() throws Exception;

    /**
     * Is called after every module has initialized or when this module starts at a later moment.
     * @return <code>true</code> when it was successfully started.
     * @throws Exception when somethings goes wrong.
     */
    public boolean start() throws Exception {
        upSince = System.currentTimeMillis();
        return true;
    }

    /**
     * Is called when the program stops or this modules stops.
     * @throws Exception when somethings goes wrong.
     */
    public void stop() throws Exception {
        upSince = null;
    }

    /**
     * Is called when a Exception is thrown in {@link #init()}, {@link #start()} or {@link #stop()}.
     * @throws Exception when somethings goes wrong.
     */
    public void onCrash() throws Exception {
        upSince = null;
    }

    public abstract Status getStatus();
}
