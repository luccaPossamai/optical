package net.lpcamors.optical.content.blocks;

import net.lpcamors.optical.content.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.content.blocks.optical_source.OpticalSourceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public interface IBeamReceiver {

    static float BEAM_RADIUS = 0.05F;
    float LIVING_ENTITY_EXTENDED_RADIUS = 2F;


    void receive(OpticalSourceBlockEntity opticalLaserSourceBlockEntity, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, List<BlockPos> toRemove, int lastIndex);

    static Vec3 getLaserIrradiatedFaceOffset(Direction faceIrradiated, BlockPos pos, Level level){
        final AABB[] aabb = new AABB[1];
        getNearLivingEntity(level, pos, LIVING_ENTITY_EXTENDED_RADIUS, faceIrradiated).ifPresentOrElse(
                livingEntity -> {
                    AABB aabb1 = livingEntity.getBoundingBox();
                    aabb1 = aabb1.move(-pos.getX(), -pos.getY(), -pos.getZ());
                    //aabb1.move(livingEntity.getX() - pos.getX(), livingEntity.getY() - pos.getY(), livingEntity.getZ() - pos.getZ());
                    aabb[0] = aabb1;

                },
                () -> {
                    BlockState state = level.getBlockState(pos);
                    if(state.getBlock() instanceof IBeamReceiver || state.isAir()) {
                        aabb[0] = new AABB(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
                    } else {
                        BlockGetter bg = level.getChunk(pos);
                        aabb[0] = state.getShape(bg, pos).bounds();
                    }
                });
        boolean f1 = faceIrradiated.getAxisDirection().getStep() > 0;
        Vec3 vec3 = (!f1 ? new Vec3(aabb[0].maxX, aabb[0].maxY, aabb[0].maxY) : new Vec3(aabb[0].minX, aabb[0].minY, aabb[0].minZ)).multiply(Vec3.atLowerCornerOf(faceIrradiated.getNormal()));
        return vec3.subtract(new Vec3(0.5, 0.5, 0.5).multiply(Vec3.atLowerCornerOf(faceIrradiated.getNormal())));
    }

    static Optional<LivingEntity> getNearLivingEntity(Level level, BlockPos pos, double extendedRadius, Direction direction){
        AABB aabb = new AABB(pos.getCenter().add(new Vec3(extendedRadius, extendedRadius, extendedRadius)), pos.getCenter().add(new Vec3(-extendedRadius, -extendedRadius, -extendedRadius)));
        LivingEntity livingEntity = level.getNearestEntity(LivingEntity.class, TargetingConditions.forNonCombat(), null, pos.getX(), pos.getY(), pos.getZ(), aabb);
        if(livingEntity == null) return Optional.empty();
        return insideOfBounds(pos, direction, livingEntity.getBoundingBox()) ? Optional.of(livingEntity) : Optional.empty();
    }

    static boolean insideOfBounds(BlockPos pos, Direction direction, AABB aabb){
        Vec3 u = Vec3.atLowerCornerOf(direction.getAxis().isHorizontal() ? direction.getNormal() : direction.getNormal()).scale(direction.getAxisDirection().getStep() < 0 ? -1 : 1);//GET UNITARY
        Vec3 posV = pos.getCenter();
        AABB aabb1 = new AABB(posV.subtract(u.multiply(0.5, 0.5, 0.5)), posV.add(u.multiply(0.5, 0.5, 0.5)));
        aabb1 = aabb1.inflate(BEAM_RADIUS);
        return aabb.intersects(aabb1) || aabb1.intersects(aabb);
    }

    static boolean areBoundingBoxesTouching(AABB box1, AABB box2) {
        return box2.contains(box1.minX, box1.minY, box1.minZ) ||
                box2.contains(box1.minX, box1.minY, box1.maxZ) ||
                box2.contains(box1.minX, box1.maxY, box1.minZ) ||
                box2.contains(box1.minX, box1.maxY, box1.maxZ) ||
                box2.contains(box1.maxX, box1.minY, box1.minZ) ||
                box2.contains(box1.maxX, box1.minY, box1.maxZ) ||
                box2.contains(box1.maxX, box1.maxY, box1.minZ) ||
                box2.contains(box1.maxX, box1.maxY, box1.maxZ);
    }


}
