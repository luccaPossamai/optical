package net.lpcamors.optical.content.blocks.optical_sensor;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.content.blocks.COBlocks;
import net.lpcamors.optical.content.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.content.blocks.optical_source.OpticalSourceBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;


public class OpticalSensorBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    private Optional<BeamHelper.BeamProperties> optionalBeamProperties = Optional.empty();
    private @Nullable BlockPos sourceBlockPos = null;
    public OpticalSensorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public void tick() {
        super.tick();
        if(this.updateState()){
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(BlockStateProperties.LIT, this.getSignal() > 0), 3);
            this.updateNeighbours(this.getBlockState(), this.level);
        }
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

    public Optional<BeamHelper.BeamProperties> getOptionalBeamProperties() {
        return optionalBeamProperties;
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

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        this.optionalBeamProperties = BeamHelper.BeamProperties.read(compound);
    }
    @Override
    public void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        this.optionalBeamProperties.ifPresent(beamProperties -> beamProperties.write(compound));
    }

    public int getSignal() {
        return this.getBlockState().getValue(OpticalSensorBlock.MODE).apply(this.optionalBeamProperties);
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
