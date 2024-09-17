package net.lpcamors.optical.blocks.optical_sensor;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.blocks.COBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;
import java.util.Optional;


public class OpticalSensorBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    private IBeamReceiver.BeamSourceInstance beamSourceInstance = IBeamReceiver.BeamSourceInstance.empty(null);

    public OpticalSensorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

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
        this.setChanged();
        this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(BlockStateProperties.LIT, this.getSignal() > 0), 3);
        this.updateNeighbours(this.getBlockState(), this.level);
    }

    public boolean changeState(BlockPos pos, BeamHelper.BeamProperties beamProperties){
        if(this.beamSourceInstance.optionalBeamProperties().isEmpty()){
            this.beamSourceInstance = new IBeamReceiver.BeamSourceInstance(Optional.of(beamProperties), pos);
            update();
        }
        return beamProperties.equals(this.beamSourceInstance.optionalBeamProperties().orElse(null));
    }

    public Optional<BeamHelper.BeamProperties> getOptionalBeamProperties() {
        return this.beamSourceInstance.optionalBeamProperties();
    }


    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.beamSourceInstance = IBeamReceiver.BeamSourceInstance.read(compound);
    }
    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        this.beamSourceInstance.write(compound);
    }

    public int getSignal() {
        return this.getBlockState().getValue(OpticalSensorBlock.MODE).apply(this.beamSourceInstance.optionalBeamProperties());
    }

    private void updateNeighbours(BlockState p_54681_, Level p_54682_) {
        p_54682_.updateNeighborsAt(this.getBlockPos(), COBlocks.OPTICAL_SENSOR.get());
        p_54682_.updateNeighborsAt(this.getBlockPos().relative(p_54681_.getValue(DirectionalBlock.FACING).getOpposite()), COBlocks.OPTICAL_SENSOR.get());
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Lang.builder("tooltip").translate(COMod.ID +".gui.goggles.optical_sensor").forGoggles(tooltip);

        Lang.text("").add(Components.translatable(("create." + COMod.ID + ".gui.goggles.optical_sensor.mode")).withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
        Lang.text("").add(Components.translatable(this.getBlockState().getValue(OpticalSensorBlock.MODE).getDescriptionId()).withStyle(ChatFormatting.AQUA)).forGoggles(tooltip, 1);

        return true;
    }



}
