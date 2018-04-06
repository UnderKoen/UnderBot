package nl.underkoen.auth;

import nl.underkoen.underbot.models.Module;
import nl.underkoen.underbot.models.ModuleInfo;
import nl.underkoen.underbot.models.Status;
import nl.underkoen.underbot.utils.ModuleFileUtil;

/**
 * Created by Under_Koen on 10/03/2018.
 */
public class Main extends Module {

    public Main(ModuleInfo moduleInfo, ModuleFileUtil moduleFileUtil) {
        super(moduleInfo, moduleFileUtil);
    }

    @Override
    public boolean init() {
        status = Status.INITIALIZING;
        return true;
    }

    @Override
    public boolean start() throws Exception {
        super.start();
        status = Status.RUNNING;
        return true;
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        status = Status.STOPPED;
    }

    @Override
    public void onCrash() throws Exception {
        super.onCrash();
        status = Status.CRASHED;
    }

    private Status status = Status.NONE;

    @Override
    public Status getStatus() {
        return status;
    }
}
