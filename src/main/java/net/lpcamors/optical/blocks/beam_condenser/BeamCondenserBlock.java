package net.lpcamors.optical.blocks.beam_condenser;

import com.simibubi.create.foundation.block.IBE;
import net.lpcamors.optical.COShapes;
import net.lpcamors.optical.blocks.COBlockEntities;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.IBeamSource;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;


public class BeamCondenserBlock extends HorizontalDirectionalBlock implements IBeamReceiver, IBE<BeamCondenserBlockEntity> {

    public BeamCondenserBlock(Properties p_54120_) {
        super(p_54120_);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState p_60555_, @NotNull BlockGetter p_60556_, @NotNull BlockPos p_60557_, @NotNull CollisionContext p_60558_) {
        return (COShapes.BEAM_CONDENSER).get(p_60555_.getValue(FACING));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_.add(FACING));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void receive(IBeamSource iBeamSource, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, int lastIndex) {
        Direction direction = beamProperties.direction;
        BeamCondenserBlockEntity be = this.getBlockEntity(iBeamSource.getLevel(), lastPos);
        if(iBeamSource instanceof BeamCondenserBlockEntity || be == null || state.getValue(FACING).equals(direction.getOpposite())) return;

        BlockPos pos = be.getBlockPos();
        if(be.changeState(direction, iBeamSource.getBlockPos(), beamProperties)){
            iBeamSource.addDependent(pos);
        }
    }

    @Override
    public boolean isPathfindable(BlockState p_60475_, BlockGetter p_60476_, BlockPos p_60477_, PathComputationType p_60478_) {
        return super.isPathfindable(p_60475_, p_60476_, p_60477_, p_60478_);
    }

    @Override
    public Class<BeamCondenserBlockEntity> getBlockEntityClass() {
        return BeamCondenserBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BeamCondenserBlockEntity> getBlockEntityType() {
        return COBlockEntities.BEAM_CONDENSER.get();
    }
}
