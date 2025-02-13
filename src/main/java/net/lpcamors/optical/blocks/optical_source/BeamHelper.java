package net.lpcamors.optical.blocks.optical_source;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.lpcamors.optical.CODamageSources;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.COIcons;
import net.lpcamors.optical.COUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.*;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
    public static Vec3i colorSum(Vec3i rgb1, Vec3i rgb2){
        // THIS IS PURELY DECORATION(to make a real sum of colors you'll need to save the changes of colors)
        // That's not hard to make, I just don't want that :/
        if(rgb1.distToLowCornerSqr(250, 250, 250) <= 125) return rgb2;
        Vec3 v = Vec3.atLowerCornerOf(rgb1).add(Vec3.atLowerCornerOf(rgb2)).multiply(0.5F,0.5F, 0.5F);
        return new Vec3i((int)  v.x, (int) v.y, (int) v.z);
    }
    public static class BeamProperties {

        public static float SPEED_CONSTANT = 4F;

        public final float intensity;
        public final BeamPolarization beamPolarization;
        public Vec3i color;
        public final Direction direction;
        public final Boolean spin;
        public final BeamType beamType;
        public int changedColors = 1;

        public boolean forceVisibility = false;
        public boolean forcePenetration = false;

        public static float intensityBySpeed(float speed){
            return Math.abs(speed / SPEED_CONSTANT);
        }
        public static boolean spinBySpeed(float speed){
            return speed > 0;
        }
        public static BeamProperties withDirection(BeamProperties beamProperties, Direction direction){
            return new BeamHelper.BeamProperties(beamProperties.intensity, beamProperties.beamPolarization, beamProperties.color, direction, beamProperties.spin, beamProperties.beamType);
        }

        public BeamProperties(float intensity, BeamPolarization beamPolarization, Direction direction, boolean spin, BeamType beamType){
            this(intensity, beamPolarization, COUtils.getColor(DyeColor.WHITE), direction, spin, beamType);
        }

        public BeamProperties(float intensity, BeamPolarization beamPolarization, Vec3i color, Direction direction, boolean spin, BeamType beamType){
            this.intensity = intensity;
            this.beamPolarization = beamPolarization;
            this.color = color;
            this.direction = direction;
            this.spin = spin;
            this.beamType = beamType;
        }


        public static BeamProperties sum(Direction direction, List<BeamProperties> beamProperties) {

            float intensity = 0;
            BeamPolarization beamPolarization = beamProperties.get(0).beamPolarization;
            boolean forcePol = false;
            Vec3i color = null;
            BeamType type = beamProperties.get(0).beamType;
            boolean hasVisible = false;
            boolean spin = true;
            for(BeamProperties beamProperties1 : beamProperties){
                if(intensity < beamProperties1.intensity) {
                    type = beamProperties1.beamType;
                    spin = beamProperties1.spin;
                }

                intensity += beamProperties1.intensity;
                if(!forcePol && beamPolarization != beamProperties1.beamPolarization && beamProperties1.beamPolarization != BeamPolarization.RANDOM) {
                    if(beamPolarization != BeamPolarization.RANDOM){
                        beamPolarization = BeamPolarization.RANDOM;
                        forcePol = true;
                    } else {
                        beamPolarization = beamProperties1.beamPolarization;
                    }
                }
                if(beamProperties1.beamType.visible()){
                    hasVisible = true;
                    if(color == null) {
                        color = beamProperties1.color;
                    } else  {
                        color = colorSum(color, beamProperties1.color);
                    }
                }
            }
            color = color == null ? COUtils.getColor(DyeColor.WHITE) : color;
            BeamProperties beamProperties1 = new BeamProperties(intensity, beamPolarization, color, direction, spin, type);
            beamProperties1.forceVisibility = hasVisible;

            beamProperties1.forcePenetration = type.canPassThroughEntities();//type cannot be null, dumb IDE
            return beamProperties1;
        }

        public float getTheoreticalIntensitySpeed(){
            return SPEED_CONSTANT * this.intensity * (this.spin ? 1 : -1);
        }


        public BeamType getType(){
            return this.beamType;
        }

        public boolean isVisible() {
            return this.beamType.visible() || this.forceVisibility;
        }

        public boolean canPassThroughEntities(){
            return this.beamType.canPassThroughEntities() || this.forcePenetration;
        }

        @Override
        public boolean equals(Object obj) {
            BeamProperties beamProperties = obj instanceof BeamProperties ? (BeamProperties) obj : null;
            if(obj == null) return false;
            boolean f = beamProperties.intensity == this.intensity
                    && beamProperties.beamPolarization == this.beamPolarization
                    && beamProperties.color.equals(this.color)
                    && beamProperties.direction == this.direction && beamProperties.beamType == this.beamType
                    && beamProperties.forceVisibility == this.forceVisibility && beamProperties.forcePenetration == this.forcePenetration
                    && beamProperties.spin == this.spin;
                    ;
            return f;
        }

        public void write(CompoundTag compoundTag){
            ListTag listTag = new ListTag();
            ListTag tagFloats = new ListTag();
            ListTag tagInts = new ListTag();
            ListTag tagBoolean = new ListTag();
            ListTag color = NBTHelper.writeVec3i(this.color);
            tagFloats.add(FloatTag.valueOf(this.intensity));
            tagInts.add(IntTag.valueOf(this.beamPolarization.id));
            tagInts.add(IntTag.valueOf(this.direction == null ? 0 : List.of(Direction.values()).indexOf(direction)));
            tagInts.add(IntTag.valueOf(this.forceVisibility ? 1 : 0));
            tagInts.add(IntTag.valueOf(this.forcePenetration ? 1 : 0));
            tagInts.add(IntTag.valueOf(this.beamType.id));
            tagInts.add(IntTag.valueOf(this.spin ? 1 : 0));

            listTag.add(tagFloats);
            listTag.add(tagInts);
            listTag.add(tagBoolean);
            listTag.add(color);
            compoundTag.put("BeamProperties", listTag);
        }

        public static Optional<BeamProperties> read(CompoundTag compoundTag){
            try {
                if(!compoundTag.contains("BeamProperties")) return Optional.empty();
                ListTag listTag = (ListTag) compoundTag.get("BeamProperties");
                ListTag tagFloats = listTag.getList(0);
                ListTag tagInts = listTag.getList(1);
                ListTag tagBoolean = listTag.getList(2);
                ListTag color = listTag.getList(3);
                BeamProperties beamProperties = new BeamProperties(((FloatTag) tagFloats.get(0)).getAsFloat(), BeamPolarization.values()[((IntTag)tagInts.get(0)).getAsInt()], NBTHelper.readVec3i(color), Direction.values()[tagInts.getInt(2)], tagInts.getInt(6) != 0, BeamType.values()[((IntTag)tagInts.get(5)).getAsInt()]);
                beamProperties.forceVisibility = tagInts.getInt(3) == 1;
                beamProperties.forcePenetration = tagInts.getInt(4) == 1;
                return Optional.of(beamProperties);
            } catch (Exception e) {
                e.fillInStackTrace();
                System.out.println("Hi, that is a common error. It happens when you're loading an older version world.");
            }
            return Optional.empty();
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
            return getTranslationKey();
        }

        @Override
        public AllIcons getIcon() {
            return this.coIcons;
        }

        @Override
        public String getTranslationKey() {
            return "polarization." + this.getSerializedName();
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
        public final int id;
        public final BiConsumer<LivingEntity, BeamProperties> livingEntityBiConsumer;
        public final BiConsumer<BlockState, BeamProperties> blockStateBiConsumer;
        public final BiConsumer<ItemEntity, BeamProperties> itemEntityBiConsumer;
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
            return "beam_type.type."+(this.name().toLowerCase(Locale.ROOT));
        }



    }



}
