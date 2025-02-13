package net.lpcamors.optical.blocks.hologram_source;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.lpcamors.optical.COUtils;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.data.COLang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.units.qual.C;

import javax.annotation.Nullable;
import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class HologramSourceBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation, IHaveHoveringInformation {

    private IBeamReceiver.BeamSourceInstance beamSourceInstance = IBeamReceiver.BeamSourceInstance.empty(null);

    private HologramSourceProfile profile = new HologramSourceProfile(this.getBlockPos());
    private boolean isController = true;


    public HologramSourceBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    private boolean shouldBeController(){
        BlockState state = this.getBlockState();
        if(!(state.getBlock() instanceof HologramSourceBlock hologramSourceBlock)) return false;
        return HologramSourceBlock.getConnection(state, this.getBlockPos(), this.level, Direction.AxisDirection.NEGATIVE).isEmpty();
    }
    private int calculateConnectionLength(){
        BlockState state = this.getBlockState();
        if(!(state.getBlock() instanceof HologramSourceBlock hologramSourceBlock)) return 0;
        HologramSourceBlockEntity be;

        BlockPos pos = this.getBlockPos();
        AtomicReference<Integer> l = new AtomicReference<>(0);
        onConnection(pos, false, hologramSourceBlockEntity -> l.updateAndGet(v -> v + 1));
        return l.get();
    }

    public void onConnection(BlockPos pos, boolean toConnector, Consumer<Optional<HologramSourceBlockEntity>> consumer){
        Direction.AxisDirection direction = toConnector ? Direction.AxisDirection.NEGATIVE : Direction.AxisDirection.POSITIVE;
        Optional<HologramSourceBlockEntity> be;
        BlockPos pos1 = pos;
        do {
            be = HologramSourceBlock.getConnection(this.getBlockState(), pos1, this.level, direction);
            if(be.isPresent()) pos1 = be.get().getBlockPos();
            //pos1 = pos1.relative(Direction.fromAxisAndDirection(HologramSourceBlock.getConnectionAxis(this.getBlockState()),direction));
            consumer.accept(be);
        } while (be.isPresent());
    }

    public boolean isController() {
        return isController;
    }

    public void onAdded(){
        if(this.getLevel() == null || getLevel().isClientSide) return;
        AtomicReference<HologramSourceBlockEntity> newController = new AtomicReference<>(this);
        final HologramSourceProfile[] lastProfile = {null};
        List.of(Direction.AxisDirection.POSITIVE, Direction.AxisDirection.NEGATIVE).forEach(axisDirection -> {
            HologramSourceBlock.getConnection(this.getBlockState(), this.getBlockPos(), this.getLevel(), axisDirection)
                    .ifPresent(be -> {
                        HologramSourceBlockEntity controller = be.getController();
                        if(controller == null) return;
                        newController.set(controller);
                        if(!controller.shouldBeController()) {
                            newController.set(controller.findController());
                            lastProfile[0] = controller.profile;
                        }
                    });
        });
        if(lastProfile[0] != null) newController.get().profile.update(lastProfile[0]);
        newController.get().updateConnection(newController.get());
        onConnection(newController.get().getBlockPos(), false, opbe -> opbe.ifPresent(be -> be.updateConnection(newController.get())));
    }




    public HologramSourceBlockEntity findController(){
        AtomicReference<HologramSourceBlockEntity> newController = new AtomicReference<>(this);
        onConnection(this.getBlockPos(), true, b -> b.ifPresent(newController::set));
        return newController.get();
    }




    public void updateConnection(HologramSourceBlockEntity controller){
        if(this.getLevel() != null && !getLevel().isClientSide){
            this.isController = controller.getBlockPos() == this.getBlockPos();
            this.setControllerPos(controller.getBlockPos());
            if(this.isController){
                this.setConnectionLength(calculateConnectionLength());

            } else {
                this.profile.update(controller.profile);
            }
            sendData();
        }
    }
    @Override
    public void tick() {
        super.tick();
        if(!this.level.isClientSide){
            //this.isController = this.getControllerPos().equals(this.getBlockPos());
            //this.sendData();
        }
        if(this.shouldUpdate()){
            this.update();
        }


    }


    public @Nullable HologramSourceBlockEntity getController(){
        if(this.isController || ! (this.getBlockState().getBlock() instanceof HologramSourceBlock b)) return this;
        return b.getBlockEntity(this.getLevel(), this.getControllerPos());

    }
    @Override
    public AABB getRenderBoundingBox() {
        if(!this.isController) return super.getRenderBoundingBox();
        return getProjectionBox().inflate(0, 1, 0);
    }

    public AABB getProjectionBox(){
        Vec3 center = Vec3.atCenterOf(this.getBlockPos()).add(COUtils.getAbsVec(Vec3.atLowerCornerOf(this.getBlockState().getValue(HologramSourceBlock.FACING).getCounterClockWise().getNormal())).scale((this.getConnectionLength() - 1)/ 2D));
        center = center.add(0,0.5 + this.getConnectionLength() / 2D,0);
        return new AABB(center, center).inflate(getConnectionLength() / 2D);
    }
    public boolean shouldUpdate(){
        IBeamReceiver.BeamSourceInstance beamSourceInstance1 = this.beamSourceInstance;
        this.beamSourceInstance = this.beamSourceInstance.checkSourceExistenceAndCompatibility(this);
        return !beamSourceInstance1.equals(this.beamSourceInstance);

    }

    public void update(){
        this.setChanged();
    }

    public HologramSourceProfile getProfile() {
        return profile;
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


    public boolean isActive(){
        return this.getOptionalBeamProperties().isPresent();
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {}

    public Integer getConnectionLength(){
        return this.profile.connectionLength;
    }

    public void setConnectionLength(Integer integer){
        this.profile.connectionLength = integer;
    }

    public BlockPos getControllerPos(){
        return this.profile.controllerPos;
    }


    public void setControllerPos(BlockPos pos){
        this.profile.controllerPos = pos;
    }


    public int getFixedAngle() {
        return this.profile.fixedAngle;
    }

    public void setFixedAngle(int fixedAngle) {
        this.profile.fixedAngle = fixedAngle;
    }

    public void setItemStack(ItemStack itemStack) {
        try {
            HologramSourceBlockEntity controller = this.getController();
            controller.profile.stack = itemStack;
            controller.sendData();
            controller.onConnection(controller.getBlockPos(), false, opBe -> opBe.ifPresent(be1 -> {
                be1.profile.update(controller.profile);
                be1.sendData();
            }));

        } catch (Exception ex){
            System.out.println("Unable to send data to server in "+this.toString());
        }
    }

    public ItemStack getItemStack() {
        return this.profile.stack;
    }
    public Mode getMode() {
        return this.profile.displayMode;
    }

    public void setMode(int mode) {
        this.profile.displayMode = Mode.values()[Math.max(0, Math.min(Mode.values().length, mode))];
    }

    public CompoundTag getModeData(){
        CompoundTag tag = new CompoundTag();
        tag.putInt("ModeIndex", this.getMode().ordinal());
        tag.putInt("Angle", this.getFixedAngle());
        return tag;
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {

        HologramSourceProfile.read(tag).ifPresent(hologramSourceProfile -> {
            this.profile.update(hologramSourceProfile);
            this.isController = hologramSourceProfile.controllerPos.equals(this.getBlockPos());
        });
        super.read(tag, clientPacket);
    }


    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {

        this.profile.write(tag);
        super.write(tag, clientPacket);
    }

    public static class HologramSourceProfile {

        public BlockPos controllerPos;
        public Integer connectionLength = 1;
        public ItemStack stack = ItemStack.EMPTY;
        public Mode displayMode = Mode.ROTATING_COUNTERCLOCKWISE;
        public Integer fixedAngle = 0;



        public HologramSourceProfile(BlockPos controllerPos){
            this.controllerPos = controllerPos;
        }
        public HologramSourceProfile update(HologramSourceProfile profile) {
            return this.update(profile.controllerPos, profile.connectionLength, profile.stack, profile.displayMode, profile.fixedAngle);
        }

        public HologramSourceProfile update(BlockPos controllerPos, Integer connectionLength, ItemStack stack, Mode displayMode, Integer fixedAngle) {
            this.controllerPos = controllerPos;
            this.connectionLength = connectionLength;
            this.stack = stack;
            this.displayMode = displayMode;
            this.fixedAngle = fixedAngle;
            return this;
        }


        public void write(CompoundTag tag){
            ListTag profile = new ListTag();
            ListTag intTags = new ListTag();
            ListTag compoundTags = new ListTag();
            intTags.add(IntTag.valueOf(this.connectionLength));
            compoundTags.add(this.stack.save(new CompoundTag()));
            CompoundTag tagEnum = new CompoundTag();
            NBTHelper.writeEnum(tagEnum, "DisplayMode", this.displayMode);
            compoundTags.add(tagEnum);
            intTags.add(IntTag.valueOf(this.fixedAngle));
            profile.add(NBTHelper.writeVec3i(this.controllerPos));
            profile.add(intTags);
            profile.add(compoundTags);
            tag.put("Profile", profile);
        }
        public static Optional<HologramSourceProfile> read(CompoundTag tag){
            HologramSourceProfile profile = null;
            if(tag.contains("Profile")){
                ListTag profileTag  = (ListTag) tag.get("Profile");
                try{
                    ListTag ints = profileTag.getList(1);
                    ListTag compounds = profileTag.getList(2);
                    profile = new HologramSourceProfile(BlockPos.ZERO).update(
                            new BlockPos(NBTHelper.readVec3i(profileTag.getList(0))), ints.getInt(0),
                            ItemStack.of(compounds.getCompound(0)), NBTHelper.readEnum(compounds.getCompound(1), "DisplayMode", Mode.class),
                            ints.getInt(1)
                    );
                } catch (Exception e) {
                    System.out.println("Unable to load data from tag. ");
                    System.out.println(e.getLocalizedMessage());
                }
            }
            return Optional.ofNullable(profile);
        }
    }

    public boolean hasFixedAngle(){
        return this.profile.displayMode.shouldRenderAngle;
    }

    public enum Mode {
        ROTATING_COUNTERCLOCKWISE("counterclockwise", false),
        ROTATING_CLOCKWISE("clockwise", false),
        SPECIFIC_ANGLE("specific_angle", true)
        ;
        final String translationKey;
        final boolean shouldRenderAngle;
        Mode(String name, boolean shouldRenderAngle){
            this.translationKey = "gui.hologram_source.mode_" + name;
            this.shouldRenderAngle = shouldRenderAngle;
        }
        public String getTranslationKey(){
            return this.translationKey;
        }

        public boolean isShouldRenderAngle() {
            return shouldRenderAngle;
        }

        public static List<Component> getComponents(){
            List<Component> components = new ArrayList<>();
            for(Mode mode: Mode.values()){
                components.add( COLang.Prefixes.OPTICAL.translate((mode.getTranslationKey())));
            }
            return components;
        }
    }

}
