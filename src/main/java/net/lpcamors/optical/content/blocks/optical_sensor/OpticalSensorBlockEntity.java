package net.lpcamors.optical.content.blocks.optical_sensor;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.lpcamors.optical.content.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.content.blocks.optical_source.OpticalSourceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static net.lpcamors.optical.content.blocks.optical_sensor.OpticalSensorBlock.INTENSITY;

public class OpticalSensorBlockEntity extends SmartBlockEntity {

    private Optional<BeamHelper.BeamProperties> optionalBeamProperties = Optional.empty();
    private @Nullable BlockPos sourceBlockPos;
    public OpticalSensorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public void tick() {
        super.tick();
        this.updateState();
        BlockState state = this.getBlockState();
        BlockState state1 = state;
        boolean f = this.optionalBeamProperties.isPresent();

        Integer blockIntensity = this.getBlockState().getValue(INTENSITY);
        Integer beamIntensity = f ? (int) (16 * this.optionalBeamProperties.get().intensity()) : 0;

        if(!blockIntensity.equals(beamIntensity)){
            state1 = state1.setValue(INTENSITY, beamIntensity);
        }
        if(state1 != state){
            if(this.hasLevel()){
                this.getLevel().setBlock(this.getBlockPos(), state1, 3);
            }
        }

    }

    public void changeState(BlockPos pos, BeamHelper.BeamProperties beamProperties){
        this.sourceBlockPos = pos;
        this.optionalBeamProperties = Optional.of(beamProperties);
        updateState();
    }
    public void updateState(){
        BlockPos pos = this.sourceBlockPos;
        if(this.hasLevel() && pos != null && this.optionalBeamProperties.isPresent()){
            BlockEntity blockEntity = this.level.getBlockEntity(pos);
            if(!(blockEntity instanceof OpticalSourceBlockEntity)) {
                this.optionalBeamProperties = Optional.empty();
            } else {
                if(!((OpticalSourceBlockEntity) blockEntity).iBeamReceiverBlockPos.contains(this.getBlockPos())){
                    this.optionalBeamProperties = Optional.empty();
                }
            }
        }
        this.setChanged();
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);

        BeamHelper.BeamProperties beamProperties = BeamHelper.BeamProperties.read(compound);
        if (beamProperties != null) {
            this.optionalBeamProperties = Optional.of(beamProperties);
        } else {
            this.optionalBeamProperties = Optional.empty();
        }

        if (!clientPacket)
            return;
        if(hasLevel())
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);

    }
    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        this.optionalBeamProperties.ifPresent(beamProperties -> beamProperties.write(compound));
    }

}
