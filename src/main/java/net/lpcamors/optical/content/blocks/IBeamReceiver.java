package net.lpcamors.optical.content.blocks;

import net.lpcamors.optical.content.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.content.blocks.optical_source.OpticalSourceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface IBeamReceiver {

    void receive(OpticalSourceBlockEntity opticalLaserSourceBlockEntity, BlockState state, BlockPos lastPos, Direction direction, BeamHelper.BeamProperties beamProperties, List<BlockPos> toRemove, int lastIndex);

}
