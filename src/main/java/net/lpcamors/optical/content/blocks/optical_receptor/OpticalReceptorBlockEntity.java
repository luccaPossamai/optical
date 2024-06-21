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
        this.updateState();
    }


    public void updateState(){
        BlockPos pos = this.sourceBlockPos;
        if(this.hasLevel() && this.optionalBeamProperties.isPresent()){
            BlockEntity blockEntity = pos == null ? null : this.level.getBlockEntity(pos);
            if(!(blockEntity instanceof OpticalSourceBlockEntity)) {
                this.optionalBeamProperties = Optional.empty();
            } else {
                if(!((OpticalSourceBlockEntity) blockEntity).iBeamReceiverBlockPos.contains(this.getBlockPos())){
                    this.optionalBeamProperties = Optional.empty();
                }
            }
        }
        this.updateGeneratedRotation();
        this.setChanged();
    }

    public void changeState(BlockPos pos, BeamHelper.BeamProperties beamProperties){
        this.sourceBlockPos = pos;
        this.optionalBeamProperties = Optional.of(beamProperties);
        updateState();
    }

    @Override
    public float getGeneratedSpeed() {
        return this.optionalBeamProperties.map(BeamHelper.BeamProperties::getIntensitySpeed).orElse(0F);
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
