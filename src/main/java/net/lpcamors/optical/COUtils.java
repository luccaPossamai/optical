package net.lpcamors.optical;

import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class COUtils {


    public static AABB radius(Vec3 vec3, double radius){
        return new AABB(vec3.x + radius, vec3.y + radius, vec3.z + radius, vec3.x - radius, vec3.y - radius, vec3.z - radius);
    }

    public static Vec3i getVec3iFromArray(List<Integer> ints){
        if(ints.size() < 3) return Vec3i.ZERO;
        return new Vec3i(ints.get(0), ints.get(1), ints.get(2));
    }

}
