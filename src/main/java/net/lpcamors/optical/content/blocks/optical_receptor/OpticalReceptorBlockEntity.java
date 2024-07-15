package net.lpcamors.optical.content.blocks.optical_receptor;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlock;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlockEntity;
import net.lpcamors.optical.content.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.content.blocks.optical_source.OpticalSourceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;

public class OpticalReceptorBlockEntity extends GeneratingKineticBlockEntity {
    private Optional<BeamHelper.BeamProperties> optionalBeamProperties = Optional.empty();
    private @Nullable BlockPos sourceBlockPos;
    public OpticalReceptorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    @Override
    public void tick() {
        super.tick();
        if(this.updateState()){
            this.updateGeneratedRotation();
        }
    }


    public boolean updateState(){
        BlockPos pos = this.sourceBlockPos;
        Optional<BeamHelper.BeamProperties> beamProperties = this.optionalBeamProperties;
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
        return beamProperties.equals(this.optionalBeamProperties);
    }

    public boolean changeState(BlockPos pos, BeamHelper.BeamProperties beamProperties){
        if(this.optionalBeamProperties.isEmpty() || this.optionalBeamProperties.get().equals(beamProperties)){
            this.sourceBlockPos = pos;
            this.optionalBeamProperties = Optional.of(beamProperties);
            updateState();
            return true;
        }
        return false;
    }

    @Override
    public float getGeneratedSpeed() {
        return this.optionalBeamProperties.map(BeamHelper.BeamProperties::getIntensitySpeed).orElse(0F);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.optionalBeamProperties = BeamHelper.BeamProperties.read(compound);

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
