package nl.underkoen.underbot.messages;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import nl.underkoen.underbot.handlers.ModuleHandler;
import nl.underkoen.underbot.models.Module;
import nl.underkoen.underbot.utils.MessageBuilder;

/**
 * Created by Under_Koen on 05/03/2018.
 */
public class ModuleAction extends Message {
    @Getter
    private String method = "moduleAction";

    private ModuleHandler moduleHandler;

    public ModuleAction(ModuleHandler moduleHandler) {
        this.moduleHandler = moduleHandler;
    }

    @Override
    public void onCall(SocketIOClient client, String data, AckRequest ackSender) {
        JsonObject json = new JsonParser().parse(data).getAsJsonObject();
        JsonObject params = json.getAsJsonObject("params");
        String action = params.get("action").getAsString();
        Long id = Long.parseLong(params.get("id").getAsString());
        Module module = moduleHandler.getModules().get(id);

        Boolean canStart = false;
        Boolean canStop = false;
        Boolean canRestart = false;
        switch (module.getStatus()) {
            case NONE:
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

        switch (action) {
            case "start":
                if (canStart) {
                    moduleHandler.init(id);
                    moduleHandler.start(id);
                }
                break;
            case "stop":
                if (canStop) {
                    moduleHandler.stop(id);
                }
                break;
            case "restart":
                if (canRestart) {
                    moduleHandler.stop(id);
                    moduleHandler.init(id);
                    moduleHandler.start(id);
                }
                break;
        }

        client.sendEvent("message", MessageBuilder.getModulesMessage(moduleHandler.getModules()));
    }
}