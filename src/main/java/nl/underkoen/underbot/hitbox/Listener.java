package nl.underkoen.underbot.hitbox;

/**
 * Created by Under_Koen on 05/11/2017.
 */
public interface Listener {
    void setup();

    void onEvent(Response response);
}
