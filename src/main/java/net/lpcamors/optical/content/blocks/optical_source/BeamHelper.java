package net.lpcamors.optical.content.blocks.optical_source;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.lpcamors.optical.CODamageSources;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.COIcons;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.*;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class BeamHelper {

    public static boolean canBeamPassThrough(@Nonnull Block block){
        List<Class<?>> classes = List.of(HalfTransparentBlock.class, SlabBlock.class, CarpetBlock.class);
        final boolean[] result = {true};
        classes.forEach(aClass -> result[0] = result[0] || (block.getClass().equals(aClass)));

        return result[0] && block instanceof TintedGlassBlock;
    }

    public static Vec3i ofDyeColor(DyeColor dyeColor){
        float[] floats = dyeColor.getTextureDiffuseColors();
        return new Vec3i((int) (floats[0] * 255), (int) (floats[1] * 255), (int) (floats[2] * 255));
    }

    public record BeamProperties(float speed, float intensity, BeamPolarization beamPolarization, DyeColor dyeColor, Direction direction){
        BeamProperties(float speed, Direction direction){
            this(speed, BeamPolarization.RANDOM, direction);
        }
        BeamProperties(float speed, BeamPolarization beamPolarization, Direction direction){
            this(speed, 1, beamPolarization, DyeColor.WHITE, direction);
        }

        public float getIntensitySpeed(){
            return this.speed * this.intensity;
        }

        public BeamType getType(){
            return BeamType.getTypeBySpeed(this.speed);
        }

        public void write(CompoundTag compoundTag){

            ListTag listTag = new ListTag();
            ListTag tagFloats = new ListTag();
            ListTag tagInts = new ListTag();
            ListTag tagDirection = new ListTag();
            tagFloats.add(FloatTag.valueOf(this.speed));
            tagFloats.add(FloatTag.valueOf(this.intensity));
            tagInts.add(IntTag.valueOf(this.beamPolarization.id));
            tagInts.add(IntTag.valueOf(this.dyeColor.getId()));
            tagDirection.add(IntTag.valueOf(this.direction == null ? 0 : List.of(Direction.values()).indexOf(direction)));

            listTag.add(tagFloats);
            listTag.add(tagInts);
            listTag.add(tagDirection);
            compoundTag.put("BeamProperties", listTag);
        }

        public static Optional<BeamProperties> read(CompoundTag compoundTag){

            if(!compoundTag.contains("BeamProperties")) return Optional.empty();
            ListTag listTag = (ListTag) compoundTag.get("BeamProperties");
            if(listTag == null) return Optional.empty();
            ListTag tagFloats = listTag.getList(0);
            ListTag tagInts = listTag.getList(1);
            ListTag tagDirection = listTag.getList(2);

            return Optional.of(new BeamProperties(((FloatTag) tagFloats.get(0)).getAsFloat(), ((FloatTag) tagFloats.get(1)).getAsFloat(), BeamPolarization.values()[((IntTag)tagInts.get(0)).getAsInt()], DyeColor.byId(((IntTag)tagInts.get(1)).getAsInt()), Direction.values()[tagDirection.getInt(0)])); //Direction.byName(listTag.getString(2))));
        }
    }




    public enum BeamPolarization implements StringRepresentable, INamedIconOptions {

        RANDOM(0, null, "", COIcons.POL_RANDOM),
        HORIZONTAL(1, 0, "⬅➡",COIcons.POL_HORIZONTAL),
        DIAGONAL_POSITIVE(2, 1, "⬋⬈", COIcons.POL_DIAGONAL_POSITIVE),
        VERTICAL(3, 2, " ⬇⬆ ", COIcons.POL_VERTICAL),
        DIAGONAL_NEGATIVE(4, 3, "⬊⬉", COIcons.POL_DIAGONAL_NEGATIVE),

        ;
        final int id;
        final @Nullable Integer angle;
        final String sIcon;
        final COIcons coIcons;
        BeamPolarization(int id, @Nullable Integer angle, String sIcon, COIcons coIcons){
            this.id = id;
            this.angle = angle;
            this.sIcon = sIcon;
            this.coIcons = coIcons;
        }

        public BeamPolarization getNextRotated(Integer angle){
            if(!this.equals(RANDOM)){
                Integer newAngle = (this.angle + angle) % 4;
                return byAngle(newAngle);
            }
            return RANDOM;
        }

        public String getsIcon() {
            return sIcon;
        }

        public int getId() {
            return id;
        }

        public BeamPolarization byAngle(Integer angle) {
            AtomicReference<BeamPolarization> beamPolarization = new AtomicReference<>(RANDOM);
            Arrays.stream(BeamPolarization.values()).forEach(laserBeamPolarization1 -> {
                if (laserBeamPolarization1.angle != null && laserBeamPolarization1.angle.equals(angle))
                    beamPolarization.set(laserBeamPolarization1);
            });
            return beamPolarization.get();
        }

        public boolean isDiagonal(){
            return this.equals(DIAGONAL_NEGATIVE) || this.equals(DIAGONAL_POSITIVE);
        }

        public @Nullable Integer getAngle() {
            return angle;
        }

        public float getRemainingIntensity(float intensity, BeamPolarization beamPolarization){
            float f;
            if (beamPolarization.equals(this) || beamPolarization.equals(RANDOM)) {
                f = 1F;
            } else if (this.equals(RANDOM)) {
                f = 0.5F;
            } else {
                f = this.angle % 2 == beamPolarization.angle % 2 ? 0F : 0.5F;
            }
            return f * intensity;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public String getDescriptionId() {
            return "create." + getTranslationKey();
        }

        @Override
        public AllIcons getIcon() {
            return this.coIcons;
        }

        @Override
        public String getTranslationKey() {
            return COMod.ID + ".polarization." + this.getSerializedName();
        }
    }

    public static BiConsumer<LivingEntity, BeamProperties> livingEntityNothing(){
        return (livingEntity, beamProperties) -> {};
    }
    public static BiConsumer<BlockState, BeamProperties> blockStateNothing(){
        return (livingEntity, beamProperties) -> {};
    }
    public static BiConsumer<ItemEntity, BeamProperties> itemEntityNothing(){
        return (livingEntity, beamProperties) -> {};
    }
    public static BiConsumer<BlockState, BeamProperties> activateRadio(){
        return (itemEntity, beamProperties) -> {};
    }
    public static BiConsumer<ItemEntity, BeamProperties> cookFood(){
        return (itemEntity, beamProperties) -> {};
    }
    public static BiConsumer<LivingEntity, BeamProperties> dealDamage(){
        return (livingEntity, beamProperties) -> {
            float damage = 3  * beamProperties.intensity <= 0.25F ? 0 : beamProperties.intensity;
            if(damage > 0) livingEntity.hurt(CODamageSources.gammaRay(livingEntity.level()), damage);
        };
    }

    public enum BeamType {

        RADIO(0, livingEntityNothing(), activateRadio(), itemEntityNothing()),
        MICROWAVE(1, livingEntityNothing(), blockStateNothing(), cookFood()),
        VISIBLE(2, livingEntityNothing(), blockStateNothing(), itemEntityNothing()),
        GAMMA(3, dealDamage(), blockStateNothing(), itemEntityNothing());
        ;
        final int id;
        final BiConsumer<LivingEntity, BeamProperties> livingEntityBiConsumer;
        final BiConsumer<BlockState, BeamProperties> blockStateBiConsumer;
        final BiConsumer<ItemEntity, BeamProperties> itemEntityBiConsumer;
        BeamType(int id, BiConsumer<LivingEntity, BeamProperties> livingEntityBiConsumer, BiConsumer<BlockState, BeamProperties> blockStateBiConsumer, BiConsumer<ItemEntity, BeamProperties> itemEntityBiConsumer){
            this.id = id;
            this.livingEntityBiConsumer = livingEntityBiConsumer;
            this.blockStateBiConsumer = blockStateBiConsumer;
            this.itemEntityBiConsumer = itemEntityBiConsumer;
        }

        public int getRange(){
            return (4 - this.id) * 32;
        }
        public static BeamType getTypeBySpeed(float speed){
            BeamType beamType = RADIO;
            for (BeamType beamType1 : BeamType.values()){
                beamType = beamType1;
                if(Math.abs(speed) <= Math.pow(2, 2 * beamType1.id + 3)){
                    break;
                }
            }
            return beamType;
        }
        public boolean visible(){
            return this.equals(VISIBLE);
        }
        public boolean canPassThroughEntities(){
            return this.equals(GAMMA);
        }

        public String getDescriptionId() {
            return "create."+COMod.ID +".beam_type.type."+(this.name().toLowerCase(Locale.ROOT));
        }

    }



}
