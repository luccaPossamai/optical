package net.lpcamors.optical.content.blocks.mirror;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.lpcamors.optical.COShapes;
import net.lpcamors.optical.content.blocks.COBlockEntities;
import net.lpcamors.optical.content.blocks.IBeamReceiver;
import net.lpcamors.optical.content.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.content.blocks.optical_source.OpticalSourceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class EncasedMirrorBlock extends DirectionalKineticBlock implements IBE<EncasedMirrorBlockEntity>, IBeamReceiver {

    public EncasedMirrorBlock(Properties p_54120_) {
        super(p_54120_);
    }



    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return COShapes.ENCASED_MIRROR.get(state.getValue(FACING));
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis().equals(state.getValue(FACING).getAxis());
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(FACING).getAxis();
    }

    @Override
    public Class<EncasedMirrorBlockEntity> getBlockEntityClass() {
        return EncasedMirrorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends EncasedMirrorBlockEntity> getBlockEntityType() {
        return COBlockEntities.ENCASED_MIRROR.get();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {

        return super.rotate(state, rot);
    }

    @Override
    public void receive(OpticalSourceBlockEntity opticalLaserSourceBlockEntity, BlockState state, BlockPos lastPos, Direction direction, BeamHelper.BeamProperties beamProperties, List<BlockPos> toRemove, int lastIndex) {
        EncasedMirrorBlockEntity mirrorBlockEntity = this.getBlockEntity(opticalLaserSourceBlockEntity.getLevel(), lastPos);
        if(mirrorBlockEntity == null) return;
        @Nullable Direction direction1 = mirrorBlockEntity.getReflectedDirection(direction, state);
        if(direction1 != null) opticalLaserSourceBlockEntity.propagateLinearBeamVar(lastPos, direction1, beamProperties, toRemove, lastIndex);

    }

}
