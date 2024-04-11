package net.lpcamors.optical.content.blocks.optical_sensor;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.redstone.diodes.AbstractDiodeBlock;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import java.util.List;

public class OpticalSensorBlock extends AbstractDiodeBlock implements IWrenchable, IBeamReceiver, IBE<OpticalSensorBlockEntity>{

    public static final IntegerProperty INTENSITY = IntegerProperty.create("intensity", 0, 16);


    public OpticalSensorBlock(Properties p_54120_) {
        super(p_54120_);
    }

    @Override
    public Class<OpticalSensorBlockEntity> getBlockEntityClass() {
        return OpticalSensorBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends OpticalSensorBlockEntity> getBlockEntityType() {
        return COBlockEntities.OPTICAL_SENSOR.get();
    }


    @Override
    protected int getDelay(BlockState p_52584_) {
        return 1;
    }

    @Override
    protected boolean shouldTurnOn(Level level, BlockPos pos, BlockState state) {
        return state.getValue(INTENSITY) > 0;
    }

    @Override
    public void receive(OpticalSourceBlockEntity opticalSourceBE, BlockState state, BlockPos lastPos, Direction direction, BeamHelper.BeamProperties beamProperties, List<BlockPos> toRemove, int lastIndex) {
        OpticalSensorBlockEntity be = this.getBlockEntity(opticalSourceBE.getLevel(), lastPos);
        if(be == null) return;
        BlockPos pos = be.getBlockPos();
        be.changeState(opticalSourceBE.getBlockPos(), beamProperties);
        if(opticalSourceBE.iBeamReceiverBlockPos.contains(pos)){
            toRemove.remove(pos);
        } else {
            opticalSourceBE.iBeamReceiverBlockPos.add(pos);
        }
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_);
        p_49915_.add(FACING, POWERED, INTENSITY);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext p_52501_) {
        BlockState state = super.getStateForPlacement(p_52501_);
        return state.setValue(INTENSITY, 0);
    }

    @Override
    public int getSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction direction) {
        Direction blockDirection = state.getValue(FACING);
        int signal = 0;
            if (direction.getAxis().equals(blockDirection.getClockWise().getAxis())) {
            signal = state.getValue(INTENSITY) - 1;
        }
        return signal;
    }
}

