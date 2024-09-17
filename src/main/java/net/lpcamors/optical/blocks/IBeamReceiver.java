package net.lpcamors.optical.blocks;

import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Optional;

public interface IBeamReceiver {

    float BEAM_RADIUS = 0.05F;
    float LIVING_ENTITY_EXTENDED_RADIUS = 2F;

    /**
     * Implement this class in IBeamReceiver respective block entity
     */
    record BeamSourceInstance(Optional<BeamHelper.BeamProperties> optionalBeamProperties, @Nullable BlockPos pos) {

        public static BeamSourceInstance empty(BlockPos pos){
            return new BeamSourceInstance(Optional.empty(), pos);
        }

        BeamSourceInstance empty() {
            return BeamSourceInstance.empty(this.pos);
        }

        public static BeamSourceInstance read(CompoundTag tag){
            int[] arr = tag.contains("SourcePos") ? tag.getIntArray("SourcePos") : null;
            return new BeamSourceInstance(BeamHelper.BeamProperties.read(tag), arr == null ? null : new BlockPos(arr[0], arr[1], arr[2]));
        }

        public void write(CompoundTag tag){
            if(this.pos != null) tag.putIntArray("SourcePos", new int[]{this.pos.getX(),this.pos.getY(), this.pos.getZ()});
            this.optionalBeamProperties.ifPresent(beamProperties -> beamProperties.write(tag));
        }

        public boolean isPropertiesValid(BeamHelper.BeamProperties beamProperties){
            return this.optionalBeamProperties.isEmpty() || this.optionalBeamProperties.get().equals(beamProperties);
        }

        //Returns itself if the optional beam properties is present and the source is 'source-ing' the BlockEntity
        public BeamSourceInstance checkSourceExistenceAndCompatibility(BlockEntity  be){
            if(be.hasLevel() && this.pos != null && this.optionalBeamProperties.isPresent()){
                BlockEntity blockEntity = be.getLevel().getBlockEntity(pos);
                if(blockEntity instanceof IBeamSource iBeamSource){
                    if(!iBeamSource.isDependent(be.getBlockPos())){
                        return this.empty();
                    }
                } else {
                    return this.empty();
                }
            }
            return this;
        }

    }


    void receive(IBeamSource iBeamSource, BlockState state, BlockPos lastPos, BeamHelper.BeamProperties beamProperties, int lastIndex);

    static Vec3 getLaserIrradiatedFaceOffset(Direction faceIrradiated, BlockPos pos, Level level){
        final AABB[] aabb = new AABB[1];
        getNearLivingEntity(level, pos, LIVING_ENTITY_EXTENDED_RADIUS, faceIrradiated).ifPresentOrElse(
                livingEntity -> {
                    boolean f = level.getBlockEntity(pos) != null && (level.getBlockEntity(pos).getBlockState().getBlock() instanceof IBeamReceiver
                                                                    ||level.getBlockEntity(pos) instanceof IBeamSource);
                    if(f) {
                        BlockState state = level.getBlockState(pos);
                        if(state.getBlock() instanceof IBeamReceiver || state.isAir()) {
                            aabb[0] = new AABB(0.5, 0.5, 0.5, 0.5, 0.5, 0.5);
                        } else {
                            BlockGetter bg = level.getChunk(pos);
                            aabb[0] = state.getShape(bg, pos).bounds();
                        }
                        return;
                    };
                    AABB aabb1 = livingEntity.getBoundingBox();
                    aabb1 = aabb1.move(-pos.getX(), -pos.getY(), -pos.getZ());
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
        Vec3 vec3 = (!f1 ? new Vec3(aabb[0].maxX, aabb[0].maxY, aabb[0].maxZ) : new Vec3(aabb[0].minX, aabb[0].minY, aabb[0].minZ)).multiply(Vec3.atLowerCornerOf(faceIrradiated.getNormal()));
        return vec3.subtract(new Vec3(0.5, 0.5, 0.5).multiply(Vec3.atLowerCornerOf(faceIrradiated.getNormal()))).scale(f1 ? 1 : -1);
    }

    static Optional<LivingEntity> getNearLivingEntity(Level level, BlockPos pos, double extendedRadius, Direction direction){
        AABB aabb = new AABB(pos.getCenter().add(new Vec3(extendedRadius, extendedRadius, extendedRadius)), pos.getCenter().add(new Vec3(-extendedRadius, -extendedRadius, -extendedRadius)));
        LivingEntity livingEntity = level.getNearestEntity(LivingEntity.class, TargetingConditions.forNonCombat(), null, pos.getX(), pos.getY(), pos.getZ(), aabb);
        if(livingEntity == null) return Optional.empty();
        BlockEntity be = level.getBlockEntity(pos);
        if(be != null && be.getBlockState().getBlock() instanceof  IBeamReceiver){
            return Optional.empty();
        }
//        if(direction.getAxis().isVertical() && (int) Math.round(livingEntity.getPosition(0).subtract(aabb.getCenter()).normalize().y) != direction.getNormal().getY()) {
//            return Optional.empty();
//        }

        return insideOfBounds(pos, direction, livingEntity.getBoundingBox()) ? Optional.of(livingEntity) : Optional.empty();
    }

    static boolean insideOfBounds(BlockPos pos, Direction direction, AABB aabb){
        Vec3 u = Vec3.atLowerCornerOf(direction.getNormal()).scale(direction.getAxisDirection().getStep());//GET UNITARY
        double l = 0.4;
        Vec3 posV = pos.getCenter();
        AABB aabb1 = new AABB(posV.subtract(u.multiply(l, l, l)), posV.add(u.multiply(l, l, l)));
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
