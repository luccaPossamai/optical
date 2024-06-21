package net.lpcamors.optical.content.blocks.optical_receptor;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import net.lpcamors.optical.content.blocks.COBlockEntities;
import net.lpcamors.optical.content.blocks.IBeamReceiver;
import net.lpcamors.optical.content.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.content.blocks.optical_source.OpticalSourceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;

public class OpticalReceptorBlock extends DirectionalAxisKineticBlock implements IBE<OpticalReceptorBlockEntity>, IBeamReceiver {

    public static final OpticalReceptorShaper SHAPER = OpticalReceptorShaper.make();

    public OpticalReceptorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level world = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos placedOnPos = context.getClickedPos()
                .relative(context.getClickedFace()
                        .getOpposite());
        BlockState placedOnState = world.getBlockState(placedOnPos);
        Block block = placedOnState.getBlock();

        if (block instanceof IRotate && ((IRotate) block).hasShaftTowards(world, placedOnPos, placedOnState, face)) {
            BlockState toPlace = defaultBlockState();
            Direction horizontalFacing = context.getHorizontalDirection();
            Direction nearestLookingDirection = context.getNearestLookingDirection();
            boolean lookPositive = nearestLookingDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            if (face.getAxis() == Direction.Axis.X) {
                toPlace = toPlace.setValue(FACING, lookPositive ? Direction.NORTH : Direction.SOUTH)
                        .setValue(AXIS_ALONG_FIRST_COORDINATE, true);
            } else if (face.getAxis() == Direction.Axis.Y) {
                toPlace = toPlace.setValue(FACING, horizontalFacing.getOpposite())
                        .setValue(AXIS_ALONG_FIRST_COORDINATE, horizontalFacing.getAxis() == Direction.Axis.X);
            } else {
                toPlace = toPlace.setValue(FACING, lookPositive ? Direction.WEST : Direction.EAST)
                        .setValue(AXIS_ALONG_FIRST_COORDINATE, false);
            }

            return toPlace;
        }

        return super.getStateForPlacement(context);
    }

    @Override
    protected Direction getFacingForPlacement(BlockPlaceContext context) {
        return context.getClickedFace();
    }

    @Override
    protected boolean getAxisAlignmentForPlacement(BlockPlaceContext context) {
        return context.getHorizontalDirection()
                .getAxis() != Direction.Axis.X;
    }



    @Override
    public Class<OpticalReceptorBlockEntity> getBlockEntityClass() {
        return OpticalReceptorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OpticalReceptorBlockEntity> getBlockEntityType() {
        return COBlockEntities.OPTICAL_RECEPTOR.get();
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPER.get(p_60555_.getValue(FACING), p_60555_.getValue(AXIS_ALONG_FIRST_COORDINATE));
    }

    @Override
    public void receive(OpticalSourceBlockEntity opticalLaserSourceBlockEntity, BlockState state, BlockPos lastPos, Direction direction, BeamHelper.BeamProperties beamProperties, List<BlockPos> toRemove, int lastIndex) {
        if(state.getValue(FACING).getAxis().isVertical()){
            if(direction.getAxis().isVertical() || direction.getAxis().equals(Direction.Axis.X) == state.getValue(AXIS_ALONG_FIRST_COORDINATE)) return;
        } else {
            if(!direction.equals(state.getValue(FACING).getOpposite())) return;
        }

        OpticalReceptorBlockEntity opticalLaserReceptorBlockEntity = this.getBlockEntity(opticalLaserSourceBlockEntity.getLevel(), lastPos);
        if(opticalLaserReceptorBlockEntity == null) return;
        BlockPos pos = opticalLaserReceptorBlockEntity.getBlockPos();
        opticalLaserReceptorBlockEntity.changeState(opticalLaserSourceBlockEntity.getBlockPos(), beamProperties);
        if(opticalLaserSourceBlockEntity.iBeamReceiverBlockPos.contains(pos)){
            toRemove.remove(pos);
        } else {
            opticalLaserSourceBlockEntity.iBeamReceiverBlockPos.add(pos);
        }
    }


}
