package net.lpcamors.optical.blocks.thermal_optical_source;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Iterate;
import net.lpcamors.optical.COShapes;
import net.lpcamors.optical.blocks.COBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;

public class ThermalOpticalSourceBlock extends HorizontalKineticBlock implements IBE<ThermalOpticalSourceBlockEntity> {


    public ThermalOpticalSourceBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(HORIZONTAL_FACING).getOpposite();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return COShapes.THERMAL_OPTICAL_SOURCE.get(state.getValue(HORIZONTAL_FACING));
    }


    public static boolean hasPipeTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        if(face == null) return true;
        return face == Direction.DOWN || state.getValue(HORIZONTAL_FACING)
                .getCounterClockWise().getAxis().equals(face.getAxis());
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferredFacing = getPreferredFacing(context);
        if (preferredFacing == null)
            preferredFacing = (Direction) Arrays.stream(context.getNearestLookingDirections()).filter(direction -> direction.getAxis().isHorizontal()).toArray()[0];
        return defaultBlockState().setValue(HORIZONTAL_FACING, context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown() ? preferredFacing : preferredFacing.getOpposite());
    }

    public Direction getPreferredFacing(BlockPlaceContext context) {
        Direction prefferedSide = null;
        for (Direction side : Iterate.directions) {
            BlockState blockState = context.getLevel()
                    .getBlockState(context.getClickedPos()
                            .relative(side));
            if (blockState.getBlock() instanceof IRotate) {
                if (((IRotate) blockState.getBlock()).hasShaftTowards(context.getLevel(), context.getClickedPos()
                        .relative(side), blockState, side.getOpposite()))
                    if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
                        prefferedSide = null;
                        break;
                    } else {
                        prefferedSide = side;
                    }
            }
        }
        return prefferedSide != null && prefferedSide.getAxis().isVertical() ? null : prefferedSide;
    }


    @Override
    public Class<ThermalOpticalSourceBlockEntity> getBlockEntityClass() {
        return ThermalOpticalSourceBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends ThermalOpticalSourceBlockEntity> getBlockEntityType() {
        return COBlockEntities.THERMAL_OPTICAL_SOURCE.get();
    }
}
