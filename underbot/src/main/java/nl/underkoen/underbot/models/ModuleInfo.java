package nl.underkoen.underbot.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Under_Koen on 02/03/2018.
 */
@AllArgsConstructor
public class ModuleInfo {
    @Getter
    @SerializedName("mainClass")
    private String mainClass;

    @Getter
    @SerializedName("name")
    private String name;

    @Getter
    @SerializedName("dependencies")
    private String[] dependencies;
}
