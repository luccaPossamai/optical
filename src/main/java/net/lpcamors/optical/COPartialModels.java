package net.lpcamors.optical;

import com.jozufozu.flywheel.core.PartialModel;
import net.minecraft.resources.ResourceLocation;

public class COPartialModels {

    public static void initiate(){}

    public static final PartialModel
            POLARIZING_FILTER = block("absorption_polarizing_filter/filter"),
            OPTICAL_SENSOR_LAMP = block("optical_sensor/lamp"),
            OPTICAL_SENSOR_LAMP_GLOW = block("optical_sensor/lamp_glow"),
            MIRROR = block("encased_mirror/mirror"),
            POLARIZING_BEAM_SPLITTER = block("polarizing_beam_splitter_block/cube"),
            FOCUS_BEAM_UI = block("beam_focuser/focus_beam_ui"),
            FOCUS_BEAM = block("beam_focuser/focus_beam"),
            OPTICAL_DEVICE_HORIZONTAL = block("heavy_optical_receptor/optical_device_horizontal"),
            FOCUS_BEAM_GLOW = block("beam_focuser/focus_beam_glow"),
            LASER_BEAM = block("laser_beam/model"),
            HOLOGRAM_BEAM = block("hologram_source/hologram_beam");

    private static PartialModel block(String path) {
        return new PartialModel(new ResourceLocation(COMod.ID, "block/" + path));
    }

}
