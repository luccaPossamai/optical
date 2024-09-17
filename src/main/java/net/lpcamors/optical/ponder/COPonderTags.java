package net.lpcamors.optical.ponder;

import com.simibubi.create.foundation.ponder.PonderRegistry;
import com.simibubi.create.foundation.ponder.PonderTag;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.blocks.COBlocks;
import net.minecraft.resources.ResourceLocation;

public class COPonderTags {

    private static PonderTag create(String id) {
        return new PonderTag(new ResourceLocation(COMod.ID, id));
    }

    public static final PonderTag
        OPTICALS = create("opticals").item(COBlocks.OPTICAL_SOURCE)
            .defaultLang("Optical Components", "Components which work with optical beams.")
            .addToIndex();
    public static void initiate(){
        PonderRegistry.TAGS.forTag(OPTICALS)
                .add(COBlocks.OPTICAL_SOURCE)
                .add(COBlocks.LIGHT_OPTICAL_RECEPTOR)
                .add(COBlocks.ENCASED_MIRROR)
                .add(COBlocks.ABSORPTION_POLARIZING_FILTER)
                .add(COBlocks.POLARIZING_BEAM_SPLITTER_BLOCK)
                .add(COBlocks.OPTICAL_SENSOR);
    }
}
