package net.lpcamors.optical.blocks.optical_source;

import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.lpcamors.optical.CODamageSources;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.COIcons;
import net.lpcamors.optical.recipes.FocusingRecipeParams;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.*;
import net.minecraft.util.Mth;
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

    public static class BeamProperties {

        public final float speed;
        public final float intensity;
        public final BeamPolarization beamPolarization;
        public final DyeColor dyeColor;
        public final Direction direction;
        public final BeamType beamType;

        public boolean forceVisibility = false;
        public boolean forcePenetration = false;

        public BeamProperties(float speed, Direction direction, BeamType beamType){
            this(speed, BeamPolarization.RANDOM, direction, beamType);
        }
        public BeamProperties(float speed, BeamPolarization beamPolarization, Direction direction, BeamType beamType){
            this(speed, 1, beamPolarization, DyeColor.WHITE, direction, beamType);
        }

        public BeamProperties(float speed, float intensity, BeamPolarization beamPolarization, DyeColor dyeColor, Direction direction) {
            this(speed, intensity, beamPolarization, dyeColor, direction, BeamType.getTypeBySpeed(speed));
        }
        public BeamProperties(float speed, float intensity, BeamPolarization beamPolarization, DyeColor dyeColor, Direction direction, BeamType beamType){
            this.speed = speed;
            this.intensity = intensity;
            this.beamPolarization = beamPolarization;
            this.dyeColor = dyeColor;
            this.direction = direction;
            this.beamType = beamType;
        }


        public static BeamProperties sum(Direction direction, List<BeamProperties> beamProperties) {
            float speed = 0;
            BeamPolarization beamPolarization = BeamPolarization.RANDOM;
            boolean forcePol = false;
            DyeColor color = null;
            BeamType type = null;
            boolean hasVisible = false;
            int signal = 1;
            for(BeamProperties beamProperties1 : beamProperties){
                if(Math.abs(speed) < Math.abs(beamProperties1.speed)) {
                    type = beamProperties1.beamType;
                    signal = (int) (beamProperties1.speed / Math.abs(beamProperties1.speed));
                }
                speed += Math.abs(beamProperties1.speed) * beamProperties1.intensity;
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
                        color = beamProperties1.dyeColor;
                    } else if(color != beamProperties1.dyeColor) {
                        color = DyeColor.WHITE;
                    }
                }
            }
            color = color == null ? DyeColor.WHITE : color;
            BeamProperties beamProperties1 = new BeamProperties(signal * speed, 1F, beamPolarization, color, direction, type);
            beamProperties1.forceVisibility = hasVisible;

            beamProperties1.forcePenetration = type.canPassThroughEntities();//type cannot be null, dumb IDE
            return beamProperties1;
        }

        public float getTeoreticalIntensitySpeed(){
            return this.speed * this.intensity;
        }
        public float getSpeed(){
            return Mth.clamp(this.getTeoreticalIntensitySpeed(), -256, 256);
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
            boolean f = beamProperties.speed == this.speed && beamProperties.intensity == this.intensity
                    && beamProperties.beamPolarization == this.beamPolarization && beamProperties.dyeColor == this.dyeColor
                    && beamProperties.direction == this.direction && beamProperties.beamType == this.beamType
                    && beamProperties.forceVisibility == this.forceVisibility && beamProperties.forcePenetration == this.forcePenetration;
                    ;
            return f;
        }

        public void write(CompoundTag compoundTag){
            ListTag listTag = new ListTag();
            ListTag tagFloats = new ListTag();
            ListTag tagInts = new ListTag();
            ListTag tagBoolean = new ListTag();
            tagFloats.add(FloatTag.valueOf(this.speed));
            tagFloats.add(FloatTag.valueOf(this.intensity));
            tagInts.add(IntTag.valueOf(this.beamPolarization.id));
            tagInts.add(IntTag.valueOf(this.dyeColor.getId()));
            tagInts.add(IntTag.valueOf(this.direction == null ? 0 : List.of(Direction.values()).indexOf(direction)));
            tagInts.add(IntTag.valueOf(this.forceVisibility ? 1 : 0));
            tagInts.add(IntTag.valueOf(this.forcePenetration ? 1 : 0));
            tagInts.add(IntTag.valueOf(this.beamType.id));

            listTag.add(tagFloats);
            listTag.add(tagInts);
            listTag.add(tagBoolean);
            compoundTag.put("BeamProperties", listTag);
        }

        public static Optional<BeamProperties> read(CompoundTag compoundTag){
            try {
                if(!compoundTag.contains("BeamProperties")) return Optional.empty();
                ListTag listTag = (ListTag) compoundTag.get("BeamProperties");
                ListTag tagFloats = listTag.getList(0);
                ListTag tagInts = listTag.getList(1);
                BeamProperties beamProperties = new BeamProperties(((FloatTag) tagFloats.get(0)).getAsFloat(), ((FloatTag) tagFloats.get(1)).getAsFloat(), BeamPolarization.values()[((IntTag)tagInts.get(0)).getAsInt()], DyeColor.byId(((IntTag)tagInts.get(1)).getAsInt()), Direction.values()[tagInts.getInt(2)], BeamType.values()[((IntTag)tagInts.get(5)).getAsInt()]);
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
            return "create."+COMod.ID +".beam_type.type."+(this.name().toLowerCase(Locale.ROOT));
        }



    }



}
