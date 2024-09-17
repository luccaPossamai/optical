package net.lpcamors.optical.blocks.optical_receptor;

import com.simibubi.create.foundation.utility.VoxelShaper;
import net.lpcamors.optical.COShapes;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;

public class OpticalReceptorShaper extends VoxelShaper {
    private VoxelShaper axisFalse, axisTrue;

    static OpticalReceptorShaper make(){
        OpticalReceptorShaper shaper = new OpticalReceptorShaper();
        shaper.axisFalse = forDirectional(COShapes.OPTICAL_RECEPTOR, Direction.UP);
        shaper.axisTrue = forDirectional(rotatedCopy(COShapes.OPTICAL_RECEPTOR, new Vec3(0, 90, 0)), Direction.UP);
        //shapes for X axis need to be swapped
        Arrays.asList(Direction.EAST, Direction.WEST).forEach(direction -> {
            VoxelShape mem = shaper.axisFalse.get(direction);
            shaper.axisFalse.withShape(shaper.axisTrue.get(direction), direction);
            shaper.axisTrue.withShape(mem, direction);
        });
        return shaper;
    }

    public VoxelShape get(Direction direction, boolean axisAlong) {
        return (axisAlong ? axisTrue : axisFalse).get(direction);
    }
}
