package net.lpcamors.optical;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.lpcamors.optical.items.COItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class COUtils {


    public static Map<ItemEntry<?>, ItemEntry<?>> EQ_SHEETS = Map.of(
            COItems.COPPER_COIL, AllItems.COPPER_SHEET,
            COItems.GOLDEN_COIL, AllItems.GOLDEN_SHEET,
            COItems.ZINC_COIL, AllItems.ZINC_INGOT,
            COItems.ROSE_QUARTZ_CATALYST_COIL, AllItems.POLISHED_ROSE_QUARTZ
            );

    public static Map<ItemEntry<?>, ItemEntry<?>> EQ_INCOMPLETE = Map.of(
            COItems.COPPER_COIL, COItems.INCOMPLETE_COPPER_COIL,
            COItems.GOLDEN_COIL, COItems.INCOMPLETE_GOLDEN_COIL,
            COItems.ZINC_COIL, COItems.INCOMPLETE_ZINC_COIL,
            COItems.ROSE_QUARTZ_CATALYST_COIL, COItems.INCOMPLETE_QUARTZ_CATALYST_COIL
    );

    public static Vec3i getColor(DyeColor dyeColor){
        return COUtils.getVec3iFromArray(IntStream.range(0, 3).mapToDouble(i -> dyeColor.getTextureDiffuseColors()[i]).mapToObj(value -> (int) (value * 255)).toList());
    }

    public static AABB radius(Vec3 vec3, double radius){
        return new AABB(vec3.x + radius, vec3.y + radius, vec3.z + radius, vec3.x - radius, vec3.y - radius, vec3.z - radius);
    }

    public static Vec3i getVec3iFromArray(List<Integer> ints){
        if(ints.size() < 3) return Vec3i.ZERO;
        return new Vec3i(ints.get(0), ints.get(1), ints.get(2));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> T getBlockEntity(BlockGetter worldIn, BlockPos pos, Class<T> c) {
        BlockEntity blockEntity = worldIn.getBlockEntity(pos);

        if (blockEntity == null)
            return null;
        if (!c.isInstance(blockEntity))
            return null;

        return (T) blockEntity;
    }

    public static Vec3 getAbsVec(Vec3 vec3){
        return new Vec3(Math.abs(vec3.x), Math.abs(vec3.y), Math.abs(vec3.z));
    }
    public static double getPseudoLengthVec(Vec3 vec3){
        return vec3.x + vec3.y + vec3.z;
    }
    public static void translatePose(PoseStack ms, Vec3 vec3){
        ms.translate(vec3.x, vec3.y, vec3.z);
    }public static void scalePose(PoseStack ms, double d){
        ms.translate(d,d,d);
    }
}
