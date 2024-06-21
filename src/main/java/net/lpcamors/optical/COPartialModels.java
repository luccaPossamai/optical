package net.lpcamors.optical;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.resources.ResourceLocation;

public class COPartialModels {

    public static void initiate(){}

    public static final PartialModel
    LASER = block("optical_source/laser"),
    POLARIZING_FILTER = block("absorption_polarizing_filter/filter"),
    OPTICAL_SENSOR_LAMP = block("optical_sensor/lamp"),
    OPTICAL_SENSOR_LAMP_GLOW = block("optical_sensor/lamp_glow"),
    MIRROR = block("encased_mirror/mirror");

    private static PartialModel block(String path) {
        return new PartialModel(new ResourceLocation(COMod.ID, "block/" + path));
    }

}
