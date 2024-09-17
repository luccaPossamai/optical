package net.lpcamors.optical;

import com.simibubi.create.AllItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.lpcamors.optical.items.COItems;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Map;

public class COUtils {


    public static Map<ItemEntry<?>, ItemEntry<?>> EQ_SHEETS = Map.of(
            COItems.COPPER_COIL, AllItems.COPPER_SHEET,
            COItems.GOLDEN_COIL, AllItems.GOLDEN_SHEET,
            COItems.ROSE_QUARTZ_CATALYST_COIL, AllItems.ROSE_QUARTZ
            );

    public static Map<ItemEntry<?>, ItemEntry<?>> EQ_INCOMPLETE = Map.of(
            COItems.COPPER_COIL, COItems.INCOMPLETE_COPPER_COIL,
            COItems.GOLDEN_COIL, COItems.INCOMPLETE_GOLDEN_COIL,
            COItems.ROSE_QUARTZ_CATALYST_COIL, COItems.INCOMPLETE_QUARTZ_CATALYST_COIL
    );

    public static AABB radius(Vec3 vec3, double radius){
        return new AABB(vec3.x + radius, vec3.y + radius, vec3.z + radius, vec3.x - radius, vec3.y - radius, vec3.z - radius);
    }

    public static Vec3i getVec3iFromArray(List<Integer> ints){
        if(ints.size() < 3) return Vec3i.ZERO;
        return new Vec3i(ints.get(0), ints.get(1), ints.get(2));
    }

}
