package nl.underkoen.underbot.models;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Under_Koen on 02/03/2018.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ModuleInfo {
    @Getter
    @SerializedName("mainClass")
    public String mainClass;

    @Getter
    @SerializedName("name")
    public String name;
}
