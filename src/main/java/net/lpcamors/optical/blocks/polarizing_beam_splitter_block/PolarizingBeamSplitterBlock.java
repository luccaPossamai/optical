package net.lpcamors.optical.blocks.polarizing_beam_splitter_block;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.lpcamors.optical.blocks.COBlockEntities;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.IBeamSource;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.COShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PolarizingBeamSplitterBlock extends HorizontalDirectionalBlock implements IWrenchable, IBeamReceiver, IBE<PolarizingBeamSplitterBlockEntity> {
    public PolarizingBeamSplitterBlock(Properties p_54120_) {
        super(p_54120_);
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_);
        p_49915_.add(FACING);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState p_60555_, @NotNull BlockGetter p_60556_, @NotNull BlockPos p_60557_, @NotNull CollisionContext p_60558_) {
        return COShapes.POLARIZING_BEAM_SPLITTER_CUBE.get(p_60555_.getValue(FACING));
    }
    @Override
    public boolean useCenteredIncidence() {
        return true;
    }
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }
    @Override
    public void receive(IBeamSource iBeamSource, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, int lastIndex) {
        Direction direction = beamProperties.direction;
        if(direction.getAxis().isVertical()) return;
        float intensity = beamProperties.beamPolarization.getRemainingIntensity(beamProperties.intensity, BeamHelper.BeamPolarization.VERTICAL);
        if(intensity > 0){
            BeamHelper.BeamProperties beamProperties1 = new BeamHelper.BeamProperties(intensity, BeamHelper.BeamPolarization.VERTICAL, beamProperties.color, direction, beamProperties.spin, beamProperties.beamType);
            IBeamSource.propagateLinearBeamVar(iBeamSource, lastPos,  beamProperties1, lastIndex);
        }
        intensity = beamProperties.beamPolarization.getRemainingIntensity(beamProperties.intensity, BeamHelper.BeamPolarization.HORIZONTAL);
        if(intensity > 0){
            direction = direction.getAxis().equals(state.getValue(FACING).getAxis()) ? direction.getClockWise() : direction.getCounterClockWise();
            BeamHelper.BeamProperties beamProperties1 = new BeamHelper.BeamProperties(intensity, BeamHelper.BeamPolarization.HORIZONTAL, beamProperties.color, direction, beamProperties.spin, beamProperties.beamType);
            IBeamSource.propagateLinearBeamVar(iBeamSource, lastPos, beamProperties1, lastIndex);
        }


    }

    @Override
    public Class<PolarizingBeamSplitterBlockEntity> getBlockEntityClass() {
        return PolarizingBeamSplitterBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends PolarizingBeamSplitterBlockEntity> getBlockEntityType() {
        return COBlockEntities.POLARIZING_BEAM_SPLITTER.get();
    }
}
