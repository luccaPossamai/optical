package net.lpcamors.optical.blocks.beam_focuser;

import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.function.Consumer;

public class BlockFocusingBehaviour {

    public static void forEach(Consumer<? super BlockFocusingBehaviour> accept){

    }

    public int focus(Level level, BlockPos pos, BeamFocuserBlockEntity be, Optional<BeamHelper.BeamProperties> beamProperties, boolean simulate){
        return 0;
    }

}
