package net.lpcamors.optical.blocks.optical_source;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.IBeamSource;
import net.lpcamors.optical.data.COTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpticalSourceBlockEntityVar extends KineticBlockEntity implements IBeamSource {

    private Map<Pair<Vec3i, Vec3i>, BeamHelper.BeamProperties> beamPropertiesMap = new HashMap<>();
    private List<BlockPos> iBeamReceiverBlockPos = new ArrayList<>();
    private List<BlockPos> toIBeamReceiverBlockPos = new ArrayList<>();
    private ScrollOptionBehaviour<BeamHelper.BeamPolarization> polarization;
    private int tickCount = 0;

    public OpticalSourceBlockEntityVar(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.polarization = new ScrollOptionBehaviour<>(BeamHelper.BeamPolarization.class,
                Lang.builder("tooltip").translate(COMod.ID +".gui.behaviour.optical_source").component(), this, new PolarizationValueBoxTransform());
        behaviours.add(polarization);

    }

    @Override
    public void tick() {
        super.tick();
        this.tickCount += 1;
        this.toIBeamReceiverBlockPos = new ArrayList<>();
        this.beamPropertiesMap.clear();
        if(Math.abs(this.getSpeed()) > 0){
            IBeamSource.propagateLinearBeamVar(this, this.getBlockPos(), this.getInitialBeamProperties(), 0);
        }
        this.iBeamReceiverBlockPos = toIBeamReceiverBlockPos;
    }


    @Override
    public BeamHelper.BeamProperties getInitialBeamProperties() {
        return new BeamHelper.BeamProperties(this.getSpeed(), this.polarization.get(), this.getBlockState().getValue(OpticalSourceBlock.HORIZONTAL_FACING));
    }


    public void propagateLinearBeamVar(BlockPos initialPos, BeamHelper.BeamProperties beamProperties, int lastIndex) {
        if(this.getInitialBeamProperties() == null) return;
        BlockPos lastPos = initialPos;
        Direction direction = beamProperties.direction;
        int range = this.getInitialBeamProperties().getType().getRange();
        BeamHelper.BeamType beamType = getInitialBeamProperties().getType();
        for (int i = 0; i + lastIndex <= range; i++) {
            lastPos = lastPos.relative(direction);
            Vec3i vec3 = lastPos;
            BlockState state = this.getLevel().getBlockState(lastPos);
            boolean penetrable = state.is(COTags.Blocks.PENETRABLE) && !state.is(COTags.Blocks.IMPENETRABLE);

            //Check if there's and living entity in the way
            LivingEntity livingEntity = IBeamReceiver.getNearLivingEntity(level, lastPos, IBeamReceiver.LIVING_ENTITY_EXTENDED_RADIUS, direction).orElse(null);
            if(livingEntity != null && (penetrable || state.getBlock() instanceof IBeamReceiver)){
                beamType.livingEntityBiConsumer.accept(livingEntity, beamProperties);
                if(!getInitialBeamProperties().canPassThroughEntities()) {
                    addToBeamBlocks(initialPos, vec3, beamProperties);
                    break;
                }
            }

            // Check if the beam passes through a ILaserReceiver
            if(state.getBlock() instanceof IBeamReceiver iBeamReceiver) {
                addToBeamBlocks(initialPos, vec3, beamProperties);
                iBeamReceiver.receive(this, state, lastPos, beamProperties, i + 1);
                this.getLevel().sendBlockUpdated(lastPos, state, state, 16);
                break;

                // Check if there is a BeaconBeamBlock in the way(colorizes the beam)
            } else if(state.getBlock() instanceof BeaconBeamBlock beaconBeamBlock) {
                this.addToBeamBlocks(initialPos, vec3, beamProperties);
                BeamHelper.BeamProperties beamProperties1 = new BeamHelper.BeamProperties(beamProperties.speed, beamProperties.intensity, beamProperties.beamPolarization, beaconBeamBlock.getColor(), direction);
                this.propagateLinearBeamVar(lastPos, beamProperties1, i + 1);
                break;

                // Check if the beam range ended
            } else if(i + lastIndex >= range || !penetrable){
                this.addToBeamBlocks(initialPos, vec3, beamProperties);
                beamType.blockStateBiConsumer.accept(this.level.getBlockState(lastPos), beamProperties);
                break;
            }
        }
    }

    @Override
    public void addToBeamBlocks(Vec3i vec, Vec3i vec1, BeamHelper.BeamProperties beamProperties) {
        this.beamPropertiesMap.put(new Pair<>(vec, vec1), beamProperties);
    }

    @Override
    public Map<Pair<Vec3i, Vec3i>, BeamHelper.BeamProperties> getBeamPropertiesMap() {
        return this.beamPropertiesMap;
    }

    @Override
    public boolean isDependent(BlockPos pos) {
        return this.iBeamReceiverBlockPos.contains(pos);
    }

    @Override
    public void addDependent(BlockPos pos) {
        this.toIBeamReceiverBlockPos.add(pos);
    }


    @Override
    public int getTickCount() {
        return this.tickCount;
    }

    @Override
    public boolean shouldRendererLaserBeam() {
        return this.getSpeed() != 0 && (this.getInitialBeamProperties() != null && this.getInitialBeamProperties().isVisible()) && !this.getBeamPropertiesMap().keySet().isEmpty();
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        ListTag listTag = new ListTag();
        this.iBeamReceiverBlockPos.forEach(pos -> {
            listTag.add(NbtUtils.writeBlockPos(pos));
        });
        compound.put("IBeamReceiverBlockPosList", listTag);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if(compound.contains("IBeamReceiverBlockPosList")) {
            ListTag listTag = (ListTag) compound.get("IBeamReceiverBlockPosList");
            if (listTag != null) {
                listTag.forEach(tag -> this.iBeamReceiverBlockPos.add(NbtUtils.readBlockPos((CompoundTag) tag)));
            }
        }
        if (!clientPacket)
            return;
        if (hasLevel())
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);
        if (!isVirtual())
            requestModelDataUpdate();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    private static class PolarizationValueBoxTransform extends ValueBoxTransform.Sided {

        @Override
        protected Vec3 getSouthLocation() {
            return VecHelper.voxelSpace(8, 6f, 15.5f);
        }


        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            if (direction.getAxis()
                    .isVertical())
                return false;
            return state.getValue(OpticalSourceBlock.HORIZONTAL_FACING).getClockWise().getAxis().equals(direction.getAxis());
        }

        @Override
        public float getScale() {
            return 0.5f;
        }

    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Lang.builder("tooltip").translate(COMod.ID +".gui.goggles.beam_properties").forGoggles(tooltip);

        if(Math.abs(this.getSpeed()) > 0){
            BeamHelper.BeamType beamType = this.getInitialBeamProperties().getType();
            Lang.text("").add(Components.translatable(("create." + COMod.ID + ".gui.goggles.beam_type")).withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
            Lang.text("").add(Components.translatable(beamType.getDescriptionId()).withStyle(ChatFormatting.AQUA)).forGoggles(tooltip, 1);
            Lang.text("").add(Components.translatable(("create." + COMod.ID + ".gui.goggles.propagation_range")).withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
            Lang.text("").add(Lang.text(" "+beamType.getRange()+" blocks").style(ChatFormatting.AQUA)).forGoggles(tooltip, 1);
        }

        BeamHelper.BeamPolarization beamPolarization = this.polarization.get();

        Lang.text("").add(Components.translatable(("create." + COMod.ID + ".gui.goggles.polarization")).withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
        Lang.text("").add(Components.translatable(beamPolarization.getDescriptionId()).append(" " + beamPolarization.getsIcon()).withStyle(ChatFormatting.AQUA)).forGoggles(tooltip, 1);

        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);

    }


}
