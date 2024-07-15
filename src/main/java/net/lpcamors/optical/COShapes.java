package net.lpcamors.optical;

import com.simibubi.create.AllShapes;
import com.simibubi.create.foundation.utility.VoxelShaper;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

public class COShapes {


    public static final VoxelShape
            OPTICAL_RECEPTOR = shape(3, 0, 0, 13, 2, 16)
            .add(3.5, 2, 1, 12.5, 13, 15)
            .build();
    public static final VoxelShaper
            OPTICAL_SOURCE = shape(0, 0, 0, 16, 12, 16)
                    .forHorizontal(Direction.NORTH),

            ENCASED_MIRROR = shape(0, 0, 0, 16, 2, 16)
                    .add(3, 2, 3, 13, 14, 13)
                    .add(0,14, 0, 16, 16, 16)
                    .add(1, 2, 1, 3, 14,3)
                    .add(13, 2, 1, 15, 14,3)
                    .add(13, 2, 13, 15, 14,15)
                    .add(1, 2, 13, 3, 14 ,15)
                    .forDirectional(Direction.UP),
            POLARIZING_BEAM_SPLITTER_CUBE = shape(0, 0, 0, 16, 2, 16)
                            .add(0,14, 0, 16, 16, 16)
                            .add(1, 2, 1, 3, 14,3)
                            .add(13, 2, 1, 15, 14,3)
                            .add(13, 2, 13, 15, 14,15)
                            .add(1, 2, 13, 3, 14 ,15)
                            .add(3, 2, 3, 13, 15, 13)
                            .forHorizontal(Direction.NORTH),
            ABSORPTION_POLARIZING_FILTER = shape(4, 0, 0, 12, 2, 16)
                    .add(4, 14, 0, 12, 16, 16)
                    .add(7, 2, 1, 9, 14, 15)
                    .forHorizontal(Direction.NORTH),
            SENSOR = shape(5, 0, 5, 11, 3, 11)
                    .add(6, 3, 6, 10, 10, 10)
                    .forDirectional(Direction.UP)
                    ;
    private static AllShapes.Builder shape(VoxelShape shape) {
        return new AllShapes.Builder(shape);
    }

    private static AllShapes.Builder shape(double x1, double y1, double z1, double x2, double y2, double z2) {
        return shape(cuboid(x1, y1, z1, x2, y2, z2));
    }

    private static VoxelShape cuboid(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Block.box(x1, y1, z1, x2, y2, z2);
    }

}
