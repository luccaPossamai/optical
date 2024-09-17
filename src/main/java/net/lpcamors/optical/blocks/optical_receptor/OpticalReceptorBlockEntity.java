package net.lpcamors.optical.blocks.optical_receptor;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class OpticalReceptorBlockEntity extends GeneratingKineticBlockEntity {

    private IBeamReceiver.BeamSourceInstance beamSourceInstance = IBeamReceiver.BeamSourceInstance.empty(null);


    public OpticalReceptorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    @Override
    public void tick() {
        super.tick();
        if(this.shouldUpdate()){
            this.update();
        }
    }



    public boolean shouldUpdate(){
        IBeamReceiver.BeamSourceInstance beamSourceInstance1 = this.beamSourceInstance;
        this.beamSourceInstance = this.beamSourceInstance.checkSourceExistenceAndCompatibility(this);
        return !beamSourceInstance1.equals(this.beamSourceInstance);

    }
    public void update(){
        updateGeneratedRotation();
        this.setChanged();

    }
    public boolean changeState(BlockPos pos, BeamHelper.BeamProperties beamProperties){
        if(this.beamSourceInstance.optionalBeamProperties().isEmpty()){
            this.beamSourceInstance = new IBeamReceiver.BeamSourceInstance(Optional.of(beamProperties), pos);
            update();
        }
        return this.beamSourceInstance.optionalBeamProperties().get().equals(beamProperties);
    }

    public OpticalReceptorBlock.OpticalReceptorGearHeaviness getGearHeaviness(){
        return ((OpticalReceptorBlock) this.getBlockState().getBlock()).heaviness;
    }

    @Override
    public float getGeneratedSpeed() {
        return this.beamSourceInstance.optionalBeamProperties().map(BeamHelper.BeamProperties::getTeoreticalIntensitySpeed).orElse(0F) / (1  + this.getGearHeaviness().getId());
    }



    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.beamSourceInstance = IBeamReceiver.BeamSourceInstance.read(compound);
        if (!clientPacket)
            return;
        if(hasLevel())
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);

    }
    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        this.beamSourceInstance.write(compound);
    }


}
