package net.lpcamors.optical.blocks.optical_receptor;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftScreen;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IExtensibleEnum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class OpticalReceptorBlockEntity extends GeneratingKineticBlockEntity {

    public final ReceptorType receptorType;

    private Map<Direction, IBeamReceiver.BeamSourceInstance> beamSourceInstanceMap = emptyMap();
    private BeamHelper.BeamProperties initialBeamProperties = null;

    public NonNullList<ItemStack> sensor = NonNullList.withSize(4, ItemStack.EMPTY);
    public Map<Direction, Integer> directionMap = new HashMap<>();
    public Map<Integer, Direction> integerDirectionMap  = new HashMap<>();

    public static OpticalReceptorBlockEntity speed(BlockEntityType<?> type, BlockPos pos, BlockState state){
        return new OpticalReceptorBlockEntity(type, pos, state, ReceptorType.SPEED);
    }

    public static OpticalReceptorBlockEntity capacity(BlockEntityType<?> type, BlockPos pos, BlockState state){
        return new OpticalReceptorBlockEntity(type, pos, state, ReceptorType.CAPACITY);
    }

    public OpticalReceptorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, ReceptorType receptorType) {
        super(type, pos, state);
        this.receptorType = receptorType;

    }

    private static Map<Direction, IBeamReceiver.BeamSourceInstance> emptyMap(){
        Map<Direction, IBeamReceiver.BeamSourceInstance> map = new HashMap<>();
        IBeamReceiver.BeamSourceInstance empty = IBeamReceiver.BeamSourceInstance.empty(null);
        for(Direction direction : Direction.values()){
            map.put(direction, empty);
        }
        return map;
    }


    public boolean addSensor(@Nonnull ItemStack itemStack, @Nonnull Direction direction){
        if(this.isVirtual()) return false;
        int i = getIndexSensorOf(direction);
        boolean f = i >= 0 && OpticalReceptorBlock.canPlaceSensorAt(direction, this.getBlockState()) && sensor.get(i).isEmpty();
        if(f) {
            this.sensor.set(i, itemStack);
            this.update();
        }
        return f;
    }

    public boolean removeSensor(@Nonnull Direction direction, Optional<Player> player){
        if(this.isVirtual()) return false;
        int i = getIndexSensorOf(direction);
        boolean f = !sensor.get(i).isEmpty();
        if(f){
            player.ifPresentOrElse(p -> p.getInventory().add(this.sensor.get(i).copy()), () -> {
                if(!this.hasLevel()) return;
                if(!this.level.isClientSide){
                    Block.popResource(this.level, this.getBlockPos(), this.sensor.get(i));
                }
            });
            this.sensor.set(i, ItemStack.EMPTY);
            this.update();
        }
        return f;
    }
    public int getIndexSensorOf(Direction direction){
        if(this.directionMap.keySet().isEmpty()) updateDirectionMap();
        return this.directionMap.get(direction);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.directionMap.keySet().isEmpty()) updateDirectionMap();
        boolean f = false;
        for(Direction direction : Direction.values()) {
            if(this.shouldUpdate(direction)){
                f = true;
            }
        }
        if(f){
            this.update();
        }
    }
    public void updateDirectionMap(){
        int i = 0;
        for(Direction direction : Direction.values()){
            if(OpticalReceptorBlock.canPlaceSensorAt(direction, this.getBlockState())){
                this.directionMap.put(direction, i);
                this.integerDirectionMap.put(i, direction);

                i++;
            } else {
                this.directionMap.put(direction, -1);
                this.integerDirectionMap.put(-1, direction);
            }
        }
    }



    public void update(){
        this.initialBeamProperties = this.getResultantBeamProperties(this.getBlockState().getValue(OpticalReceptorBlock.FACING));
        updateGeneratedRotation();
        this.setChanged();
    }
    public @Nullable BeamHelper.BeamProperties getResultantBeamProperties(Direction direction){
        List<BeamHelper.BeamProperties> beamProperties = new ArrayList<>();
        this.beamSourceInstanceMap.keySet().stream().map(direction1 -> this.beamSourceInstanceMap.get(direction1)).forEach(beamSourceInstance -> {
            beamSourceInstance.optionalBeamProperties().ifPresent(beamProperties::add);
        });
        if(beamProperties.isEmpty()) return null;
        return BeamHelper.BeamProperties.sum(direction, beamProperties.stream().toList());
    }

    public boolean shouldUpdate(Direction direction){
        IBeamReceiver.BeamSourceInstance beamSourceInstance = this.beamSourceInstanceMap.get(direction);
        this.beamSourceInstanceMap.put(direction, beamSourceInstance.checkSourceExistenceAndCompatibility(this));
        this.setChanged();
        boolean f = !beamSourceInstance.equals(this.beamSourceInstanceMap.get(direction));
        return f;
    }
    public boolean changeState(Direction direction, BlockPos pos, BeamHelper.BeamProperties beamProperties){
        int i = getIndexSensorOf(direction.getOpposite());
        if(i < 0 || i > 3) return false;
        if(this.sensor.get(i).isEmpty()) return false;
        if(this.beamSourceInstanceMap.get(direction).optionalBeamProperties().isEmpty()){
            this.beamSourceInstanceMap.put(direction, new IBeamReceiver.BeamSourceInstance(Optional.of(beamProperties), pos));
            this.update();
            return true;
        }
        return beamProperties.equals(beamSourceInstanceMap.get(direction).optionalBeamProperties().orElse(null));
    }

    public OpticalReceptorBlock.OpticalReceptorGearHeaviness getGearHeaviness(){
        return ((OpticalReceptorBlock) this.getBlockState().getBlock()).heaviness;
    }

    @Override
    public float getGeneratedSpeed() {
        Float f = this.receptorType.getSpeed(this);
        return f == null ? super.getGeneratedSpeed() : f;
    }

    @Override
    public float calculateAddedStressCapacity() {
        Float f = this.receptorType.getCapacity(this);
        return f == null ? super.calculateAddedStressCapacity() : f;
    }




    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        if(compound.contains("IBeamSourceMap")){
            ListTag listTag = (ListTag) compound.get("IBeamSourceMap");
            if (listTag != null) {
                for(int i = 0; i < listTag.size(); i++){
                    this.beamSourceInstanceMap.put(Direction.values()[i], IBeamReceiver.BeamSourceInstance.read((CompoundTag) listTag.get(i)));
                }
            }
        }
        this.initialBeamProperties = this.getResultantBeamProperties(this.getBlockState().getValue(OpticalReceptorBlock.FACING));
        if(!clientPacket){}
        if(compound.contains("SensorItems")){
            this.sensor = NonNullList.withSize(4, ItemStack.EMPTY);
            List<ItemStack> itemStacks = NBTHelper.readItemList(compound.getList("SensorItems", Tag.TAG_COMPOUND));
            for(int i = 0; i < this.sensor.size(); i++){
                this.sensor.set(i, itemStacks.get(i));
            }

        }
        if(hasLevel())
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 16);

    }
    @Override
    public void write(CompoundTag compound, boolean clientPacket) {

        ListTag listTag1 = new ListTag();
        Arrays.stream(Direction.values()).forEach(direction -> {
            CompoundTag tag = new CompoundTag();
            this.beamSourceInstanceMap.get(direction).write(tag);
            listTag1.add(tag);
        });
        compound.put("IBeamSourceMap", listTag1);
        compound.put("SensorItems",
                NBTHelper.writeItemList(this.sensor));
        super.write(compound, clientPacket);


    }

    public enum ReceptorType implements IExtensibleEnum {
        SPEED("speed",
                be -> {
                    return be.initialBeamProperties != null ? be.initialBeamProperties.getTheoreticalIntensitySpeed() : 0F;
                },
                be -> null
        ),
        CAPACITY("capacity",
                be -> {
                    return be.initialBeamProperties != null ? 32F : 0F;
                },
                be -> {
                    if (be.initialBeamProperties != null) {
                        return Math.abs(be.initialBeamProperties.getTheoreticalIntensitySpeed()) * 8f / 32f;
                    }
                    return 0F;
                }
        ),
        ;
        private final String nameId;
        private final Function<OpticalReceptorBlockEntity, Float> speed;
        private final Function<OpticalReceptorBlockEntity, Float> capacity;

        ReceptorType(String nameId, Function<OpticalReceptorBlockEntity, Float> speed, Function<OpticalReceptorBlockEntity, Float> capacity){
            this.nameId = nameId;
            this.speed = speed;
            this.capacity = capacity;
        }

        public @Nullable Float getSpeed(OpticalReceptorBlockEntity be){
            return this.speed.apply(be);
        }

        public @Nullable Float getCapacity(OpticalReceptorBlockEntity be) {
            return this.capacity.apply(be);
        }

        public static ReceptorType create(String name, String nameId, Function<OpticalReceptorBlockEntity, Float> speed, Function<OpticalReceptorBlockEntity, Float> capacity){
            throw new IllegalStateException("Enum not extended");
        }
    }




}
