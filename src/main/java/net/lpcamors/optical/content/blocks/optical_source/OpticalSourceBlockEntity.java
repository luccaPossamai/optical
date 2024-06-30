package net.lpcamors.optical.content.blocks.optical_source;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.foundation.utility.VecHelper;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.COUtils;
import net.lpcamors.optical.content.blocks.IBeamReceiver;
import net.lpcamors.optical.data.COTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

public class OpticalSourceBlockEntity extends KineticBlockEntity {


    public List<Pair<Vec3, Vec3>> blockPosToBeamLight = new ArrayList<>();
    public Map<Pair<Vec3, Vec3>, BeamHelper.BeamProperties> beamPropertiesMap = new HashMap<>();
    public BeamHelper.BeamType beamType;
    public List<BlockPos> iBeamReceiverBlockPos = new ArrayList<>();
    public int lastIBeamReceiverListSize = 0;
    public BeamHelper.BeamProperties initialBeamProperties;
    protected ScrollOptionBehaviour<BeamHelper.BeamPolarization> polarization;
    public int tickCount;
    public static final int RANGE = 32;

    public OpticalSourceBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.hasLevel() && this.iBeamReceiverBlockPos.size() != this.lastIBeamReceiverListSize){
            List<BlockEntity> blockEntities = new ArrayList<>();
            this.iBeamReceiverBlockPos.forEach(pos -> {
                BlockEntity blockEntity = this.level.getBlockEntity(pos);
                if(blockEntity != null) blockEntities.add(blockEntity);
            });
            this.lastIBeamReceiverListSize = blockEntities.size();
        }
        if(Math.abs(this.getSpeed()) > 0){
            this.beamType = BeamHelper.BeamType.getTypeBySpeed(this.getSpeed());
            this.initialBeamProperties = new BeamHelper.BeamProperties(this.getSpeed(), this.polarization.get());
            this.tickCount++;
            this.blockPosToBeamLight.clear();
            this.beamPropertiesMap.clear();
            List<BlockPos> toRemove = new ArrayList<>(this.iBeamReceiverBlockPos);
            this.propagateLinearBeamVar(this.getBlockPos(), this.getBlockState().getValue(OpticalSourceBlock.HORIZONTAL_FACING), this.initialBeamProperties, toRemove, 0);
            this.iBeamReceiverBlockPos.removeAll(toRemove);
        } else {
            this.beamType = null;
            this.iBeamReceiverBlockPos.clear();
        }
    }

    public void propagateLinearBeamVar(BlockPos initialPos, Direction direction, BeamHelper.BeamProperties beamProperties, List<BlockPos> toRemove, int lastIndex){
        BlockPos lastPos = initialPos;
        int range = this.initialBeamProperties.getType().getRange();
        for (int i = 0; i + lastIndex <= range; i++) {
            lastPos = lastPos.relative(direction);
            Vec3 vec3 = lastPos.getCenter();
            BlockState state = this.level.getBlockState(lastPos);


            //Check if there's and living entity in the way
            LivingEntity livingEntity = this.level.getNearestEntity(LivingEntity.class, TargetingConditions.forNonCombat(), null, vec3.x(), vec3.y(), vec3.z(), COUtils.radius(vec3, 0.25));
            if(livingEntity != null && state.isAir()){
                vec3 = new Vec3(Math.abs(direction.getNormal().getX()) == 0 ? vec3.x() : livingEntity.getX(), vec3.y(), Math.abs(direction.getNormal().getZ()) == 0 ? vec3.z() : livingEntity.getZ());
                this.beamType.livingEntityBiConsumer.accept(livingEntity, beamProperties);
                if(!this.beamType.canPassThroughEntities()) {
                    addToBeamBlocks(initialPos.getCenter(), vec3, beamProperties);
                    break;
                }
            }

            // Check if the beam passes through a ILaserReceiver
            if(state.getBlock() instanceof IBeamReceiver iBeamReceiver) {
                addToBeamBlocks(initialPos.getCenter(), vec3, beamProperties);
                iBeamReceiver.receive(this, state, lastPos, direction, beamProperties, toRemove, i + 1);
                this.getLevel().sendBlockUpdated(lastPos, state, state, 16);
                break;

            // Check if there is a BeaconBeamBlock in the way(colorizes the beam)
            } else if(state.getBlock() instanceof BeaconBeamBlock beaconBeamBlock) {
                this.addToBeamBlocks(initialPos.getCenter(), vec3, beamProperties);
                BeamHelper.BeamProperties beamProperties1 = new BeamHelper.BeamProperties(beamProperties.speed(), beamProperties.intensity(), beamProperties.beamPolarization(), beaconBeamBlock.getColor());
                this.propagateLinearBeamVar(lastPos, direction, beamProperties1, toRemove, i + 1);
                break;

            // Check if the beam range ended
            } else if(i + lastIndex >= range || !(state.is(COTags.Blocks.PENETRABLE)) || (state.is(COTags.Blocks.IMPENETRABLE))){
                this.addToBeamBlocks(initialPos.getCenter(), vec3, beamProperties);
                this.beamType.blockStateBiConsumer.accept(this.level.getBlockState(lastPos), beamProperties);
                break;
            }

        }
    }

    public void addToBeamBlocks(Vec3 vec, Vec3 vec1, BeamHelper.BeamProperties beamProperties){
        Pair<Vec3, Vec3> pair = new Pair<>(vec, vec1);
        this.blockPosToBeamLight.add(pair);
        this.beamPropertiesMap.put(pair, beamProperties);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.polarization = new ScrollOptionBehaviour<>(BeamHelper.BeamPolarization.class,
                Lang.builder("tooltip").translate(COMod.ID +".gui.behaviour.optical_source").component(), this, new PolarizationValueBoxTransform());
        behaviours.add(polarization);

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

    private class PolarizationValueBoxTransform extends ValueBoxTransform.Sided {

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

        if(this.beamType != null){
            Lang.text("").add(Components.translatable(("create." + COMod.ID + ".gui.goggles.beam_type")).withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
            Lang.text("").add(Components.translatable(this.beamType.getDescriptionId()).withStyle(ChatFormatting.AQUA)).forGoggles(tooltip, 1);
            Lang.text("").add(Components.translatable(("create." + COMod.ID + ".gui.goggles.propagation_range")).withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
            Lang.text("").add(Lang.text(" "+this.beamType.getRange()+" blocks").style(ChatFormatting.AQUA)).forGoggles(tooltip, 1);
        }

        BeamHelper.BeamPolarization beamPolarization = this.polarization.get();

        Lang.text("").add(Components.translatable(("create." + COMod.ID + ".gui.goggles.polarization")).withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
        Lang.text("").add(Components.translatable(beamPolarization.getDescriptionId()).append(" " + beamPolarization.getsIcon()).withStyle(ChatFormatting.AQUA)).forGoggles(tooltip, 1);

        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);

    }


}
