package net.lpcamors.optical.blocks.beam_focuser;

import com.simibubi.create.content.equipment.wrench.IWrenchable;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BeamFocuserBlock extends HorizontalKineticBlock implements IWrenchable, IBeamReceiver, IBE<BeamFocuserBlockEntity> {

    public BeamFocuserBlock(Properties p_54120_) {
        super(p_54120_);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return COShapes.FOCUSER.get(p_60555_.getValue(HORIZONTAL_FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredHorizontalFacing(context);
        if (preferred != null)
            return defaultBlockState().setValue(HORIZONTAL_FACING, preferred.getOpposite());
        return this.defaultBlockState().setValue(HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());}

    @Override
    public Class<BeamFocuserBlockEntity> getBlockEntityClass() {
        return BeamFocuserBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends BeamFocuserBlockEntity> getBlockEntityType() {
        return COBlockEntities.BEAM_FOCUSER.get();
    }

    @Override
    public void receive(IBeamSource iBeamSource, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, int lastIndex) {
        Direction direction = beamProperties.direction;
        if(!direction.equals(Direction.DOWN)){
            return;
        }
        BeamFocuserBlockEntity be = this.getBlockEntity(iBeamSource.getLevel(), lastPos);
        if(be == null) return;

        BlockPos pos = be.getBlockPos();
        if(be.changeState(iBeamSource.getBlockPos(), beamProperties)){
            iBeamSource.addDependent(pos);
        }
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(HORIZONTAL_FACING).getAxis();
    }



    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

}
