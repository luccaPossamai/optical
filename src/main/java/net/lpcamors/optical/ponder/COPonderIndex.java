package net.lpcamors.optical.ponder;

import com.simibubi.create.Create;
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.content.blocks.COBlocks;

public class COPonderIndex {

    static final PonderRegistrationHelper HELPER = new PonderRegistrationHelper(COMod.ID);
    static final PonderRegistrationHelper CREATE_HELPER = new PonderRegistrationHelper(Create.ID);

    public static void initiate() {
        HELPER.forComponents(COBlocks.OPTICAL_SOURCE, COBlocks.OPTICAL_RECEPTOR).addStoryBoard("opticals/base", COPonderScenes::base, COPonderTags.OPTICALS);
        HELPER.forComponents(COBlocks.OPTICAL_SOURCE).addStoryBoard("opticals/beam_types", COPonderScenes::beamTypes);
        HELPER.forComponents(COBlocks.ENCASED_MIRROR, COBlocks.OPTICAL_SOURCE).addStoryBoard("opticals/mirror", COPonderScenes::mirror, COPonderTags.OPTICALS);
        HELPER.forComponents(COBlocks.ABSORPTION_POLARIZING_FILTER, COBlocks.OPTICAL_SOURCE).addStoryBoard("opticals/polarizing_filter", COPonderScenes::polarizingFilter, COPonderTags.OPTICALS);
        HELPER.forComponents(COBlocks.POLARIZING_BEAM_SPLITTER_BLOCK, COBlocks.OPTICAL_SOURCE).addStoryBoard("opticals/polarizing_cube", COPonderScenes::polarizingCube, COPonderTags.OPTICALS);
        HELPER.forComponents(COBlocks.OPTICAL_SENSOR, COBlocks.OPTICAL_SOURCE).addStoryBoard("opticals/sensor", COPonderScenes::sensor, COPonderTags.OPTICALS);
    }


}
