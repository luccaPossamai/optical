package net.lpcamors.optical.blocks.beam_condenser;

import com.mojang.datafixers.util.Pair;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.IBeamSource;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.data.COLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.*;

public class BeamCondenserBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IBeamSource {

    private Map<Direction, IBeamReceiver.BeamSourceInstance> beamSourceInstanceMap = emptyMap();

    private Map<Pair<Vec3i, Vec3i>, BeamHelper.BeamProperties> beamPropertiesMap = new HashMap<>();
    private List<BlockPos> iBeamReceiverBlockPos = new ArrayList<>();
    private List<BlockPos> toIBeamReceiverBlockPos = new ArrayList<>();
    private int tickCount = 0;
    private BeamHelper.BeamProperties initialBeamProperties;

    private static Map<Direction, IBeamReceiver.BeamSourceInstance> emptyMap(){
        Map<Direction, IBeamReceiver.BeamSourceInstance> map = new HashMap<>();
        IBeamReceiver.BeamSourceInstance empty = IBeamReceiver.BeamSourceInstance.empty(null);
        for(Direction direction : Direction.values()){
            map.put(direction, empty);
        }
        return map;
    }

    public BeamCondenserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    @Override
    public void tick() {
        super.tick();
        boolean f = false;
        this.tickCount += 1;
        for(Direction direction : Direction.values()) {
            if(this.shouldUpdate(direction)){
                f = true;
            }
        }
        if(f){
            this.update();
        }
        this.toIBeamReceiverBlockPos = new ArrayList<>();
        this.beamPropertiesMap.clear();
        if(this.getInitialBeamProperties() != null && this.getInitialBeamProperties().intensity > 0){
            IBeamSource.propagateLinearBeamVar(this, this.getBlockPos(), this.getInitialBeamProperties(), 0);
        }
        this.iBeamReceiverBlockPos = toIBeamReceiverBlockPos;

    }

    public void update(){
        this.initialBeamProperties = this.getResultantBeamProperties(this.getBlockState().getValue(BeamCondenserBlock.FACING));
        this.setChanged();
    }

    public boolean shouldUpdate(Direction direction){
        IBeamReceiver.BeamSourceInstance beamSourceInstance = this.beamSourceInstanceMap.get(direction);
        this.beamSourceInstanceMap.put(direction, beamSourceInstance.checkSourceExistenceAndCompatibility(this));
        this.setChanged();
        return !beamSourceInstance.equals(this.beamSourceInstanceMap.get(direction));
    }

    public boolean changeState(Direction direction, BlockPos pos, BeamHelper.BeamProperties beamProperties){
        if(this.beamSourceInstanceMap.get(direction).optionalBeamProperties().isEmpty()){
            this.beamSourceInstanceMap.put(direction, new IBeamReceiver.BeamSourceInstance(Optional.of(beamProperties), pos));
            this.update();
            return true;
        }
        return beamProperties.equals(beamSourceInstanceMap.get(direction).optionalBeamProperties().orElse(null));
    }

    public @Nullable BeamHelper.BeamProperties getResultantBeamProperties(Direction direction){
        List<BeamHelper.BeamProperties> beamProperties = new ArrayList<>();
        this.beamSourceInstanceMap.keySet().stream().map(direction1 -> this.beamSourceInstanceMap.get(direction1)).forEach(beamSourceInstance -> {
            beamSourceInstance.optionalBeamProperties().ifPresent(beamProperties::add);
        });
        if(beamProperties.isEmpty()) return null;
        return BeamHelper.BeamProperties.sum(direction, beamProperties.stream().toList());
    }

    @Override
    public @Nullable BeamHelper.BeamProperties getInitialBeamProperties() {
        return this.initialBeamProperties;
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
        return this.getInitialBeamProperties() != null && this.getInitialBeamProperties().intensity != 0 && this.getInitialBeamProperties().isVisible() && !this.getBeamPropertiesMap().keySet().isEmpty();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);

        ListTag listTag = new ListTag();
        this.iBeamReceiverBlockPos.forEach(pos -> {
            listTag.add(NbtUtils.writeBlockPos(pos));
        });
        ListTag listTag1 = new ListTag();
        Arrays.stream(Direction.values()).forEach(direction -> {
            CompoundTag tag = new CompoundTag();
            this.beamSourceInstanceMap.get(direction).write(tag);
            listTag1.add(tag);
        });
        compound.put("IBeamReceiverBlockPosList", listTag);
        compound.put("IBeamSourceMap", listTag1);

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
        if(compound.contains("IBeamSourceMap")){
            ListTag listTag = (ListTag) compound.get("IBeamSourceMap");
            if (listTag != null) {
                for(int i = 0; i < listTag.size(); i++){
                    this.beamSourceInstanceMap.put(Direction.values()[i], IBeamReceiver.BeamSourceInstance.read((CompoundTag) listTag.get(i)));
                }
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
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        @Nullable BeamHelper.BeamProperties beamProperties = this.getResultantBeamProperties(this.getBlockState().getValue(BeamCondenserBlock.FACING));
        if(beamProperties != null){
            Lang.builder("tooltip").translate(COMod.ID +".gui.goggles.beam_properties").forGoggles(tooltip);

            Lang.text("").add(COLang.Prefixes.CREATE.translate(("gui.goggles.beam_type")).withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
            Lang.text("").add(COLang.Prefixes.CREATE.translate(beamProperties.beamType.getDescriptionId()).withStyle(ChatFormatting.AQUA)).forGoggles(tooltip, 1);
            Lang.text("").add(COLang.Prefixes.CREATE.translate(("gui.goggles.propagation_range")).withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
            Lang.text("").add(Lang.text(" "+beamProperties.beamType.getRange()+" blocks").style(ChatFormatting.AQUA)).forGoggles(tooltip, 1);

            BeamHelper.BeamPolarization beamPolarization = beamProperties.beamPolarization;

            Lang.text("").add(COLang.Prefixes.CREATE.translate(("gui.goggles.polarization")).withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
            Lang.text("").add(COLang.Prefixes.CREATE.translate(beamPolarization.getDescriptionId()).append(" " + beamPolarization.getsIcon()).withStyle(ChatFormatting.AQUA)).forGoggles(tooltip, 1);
        } else {
            return false;
        }
        return true;
    }


}
