package net.lpcamors.optical.blocks.encased_mirror;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.lpcamors.optical.COShapes;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.blocks.COBlockEntities;
import net.lpcamors.optical.blocks.IBeamSource;
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
    public void receive(IBeamSource iBeamSource, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, int lastIndex) {
        Direction direction = beamProperties.direction;
        EncasedMirrorBlockEntity mirrorBlockEntity = this.getBlockEntity(iBeamSource.getLevel(), lastPos);
        if(mirrorBlockEntity == null) return;
        @Nullable Direction direction1 = mirrorBlockEntity.getReflectedDirection(direction, state);
        BeamHelper.BeamProperties beamProperties1 = new BeamHelper.BeamProperties(beamProperties.speed, beamProperties.intensity, beamProperties.beamPolarization, beamProperties.dyeColor, direction1);
        if(direction1 != null) IBeamSource.propagateLinearBeamVar(iBeamSource, lastPos, beamProperties1, lastIndex);

    }

}
