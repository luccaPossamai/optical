package net.lpcamors.optical.blocks.optical_source;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.lpcamors.optical.blocks.COBlockEntities;
import net.lpcamors.optical.COShapes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OpticalSourceBlock extends HorizontalKineticBlock implements IBE<OpticalSourceBlockEntityVar> {


    public OpticalSourceBlock(Properties properties) {
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
        return COShapes.OPTICAL_SOURCE.get(state.getValue(HORIZONTAL_FACING));
    }


    public boolean canSurvive(BlockState p_153479_, LevelReader p_153480_, BlockPos p_153481_) {
        return Block.canSupportCenter(p_153480_, p_153481_.relative(Direction.DOWN), Direction.DOWN);
    }

    @Override
    public Class<OpticalSourceBlockEntityVar> getBlockEntityClass() {
        return OpticalSourceBlockEntityVar.class;
    }

    @Override
    public BlockEntityType<? extends OpticalSourceBlockEntityVar> getBlockEntityType() {
        return COBlockEntities.OPTICAL_SOURCE.get();
    }

}
