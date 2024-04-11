package net.lpcamors.optical;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class COUtils {


    public static AABB radius(Vec3 vec3, double radius){
        return new AABB(vec3.x + radius, vec3.y + radius, vec3.z + radius, vec3.x - radius, vec3.y - radius, vec3.z - radius);
    }

}
