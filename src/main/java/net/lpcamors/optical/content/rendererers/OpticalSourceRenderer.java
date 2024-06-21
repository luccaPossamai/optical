package net.lpcamors.optical.content.rendererers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.COPartialModels;
import net.lpcamors.optical.content.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.content.blocks.optical_source.OpticalSourceBlock;
import net.lpcamors.optical.content.blocks.optical_source.OpticalSourceBlockEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class OpticalSourceRenderer extends KineticBlockEntityRenderer<OpticalSourceBlockEntity> {
    public static final ResourceLocation LASER_BEAM_LOCATION = new ResourceLocation(COMod.ID, "textures/block/optical_source/optical_source_laser_beam.png");

    private static final RenderType LASER_BEAM_RENDER_TYPE = RenderType.beaconBeam(LASER_BEAM_LOCATION, true);
    public OpticalSourceRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(OpticalSourceBlockEntity p_112306_) {
        return true;
    }

    @Override
    protected void renderSafe(OpticalSourceBlockEntity opticalLaserSourceBlockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(opticalLaserSourceBlockEntity, partialTicks, ms, buffer, light, overlay);
        BlockState blockState = opticalLaserSourceBlockEntity.getBlockState();
        Direction direction = blockState.getValue(OpticalSourceBlock.HORIZONTAL_FACING);

        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer laser = CachedBufferer.partial(COPartialModels.LASER, blockState);

        float time = AnimationTickHolder.getRenderTime(opticalLaserSourceBlockEntity.getLevel());
        float speed = opticalLaserSourceBlockEntity.getSpeed() * 2;
        float angle = (time * speed * 3 / 10f) % 360;
        rotateLaser(laser, angle, direction).renderInto(ms, vb);
        if(shouldRendererLaserBeam(opticalLaserSourceBlockEntity)){
            renderLaserBeam(opticalLaserSourceBlockEntity, partialTicks, ms, buffer, light);
        }
    }

    public void renderLaserBeam(OpticalSourceBlockEntity opticalLaserSourceBlockEntity, float partialTicks, PoseStack ms, MultiBufferSource multiBufferSource, int light){
        Vec3 pos = opticalLaserSourceBlockEntity.getBlockPos().getCenter();
        for(int i = 0; i < opticalLaserSourceBlockEntity.blockPosToBeamLight.size(); i ++){
            Pair<Vec3, Vec3> pair = opticalLaserSourceBlockEntity.blockPosToBeamLight.get(i);
            BeamHelper.BeamProperties beamProperties = opticalLaserSourceBlockEntity.beamPropertiesMap.get(pair);
            Vec3i rgb = BeamHelper.ofDyeColor(beamProperties.dyeColor());
            int alpha = (int) (beamProperties.intensity() * 255);
            Vec3 vec = pair.getFirst();
            Vec3 vec1 = pair.getSecond();
            double x = vec1.x() - vec.x();
            double y = vec1.y() - vec.y();
            double z = vec1.z() - vec.z();
            float f = Mth.sqrt((float) (x * x + z * z));
            float f1 = Mth.sqrt((float) (x * x + y * y + z * z));
            ms.pushPose();
            ms.translate(0.5f, 0.5F, 0.5F);
            ms.translate(vec.x - pos.x, vec.y - pos.y, vec.z - pos.z);
            ms.mulPose(Axis.YP.rotation((float)(-Math.atan2(z, x) + Math.PI / 2)));
            ms.mulPose(Axis.XP.rotation((float)(Math.atan2(f, y) - Math.PI / 2)));
            VertexConsumer vertexconsumer = multiBufferSource.getBuffer(LASER_BEAM_RENDER_TYPE);
            float t = (float) ((partialTicks + opticalLaserSourceBlockEntity.tickCount) * (1 + Math.floor(Math.abs(opticalLaserSourceBlockEntity.getSpeed()) / 16)));
            float f2 = 0.0F - t * 0.01F;
            float f3 = Mth.sqrt((float) (x * x + y * y + z * z)) / 32.0F - t * 0.01F;
            float radius = 0.05f;
            float f4 = 0.0F;
            float f5 = radius;
            float f6 = 0.0F;
            PoseStack.Pose posestack$pose = ms.last();
            Matrix4f matrix4f = posestack$pose.pose();
            Matrix3f matrix3f = posestack$pose.normal();

            for(int j = 1; j <= 8; ++j) {
                float f7 = Mth.sin((float)j * ((float)Math.PI * 2F) / 8.0F) * radius;
                float f8 = Mth.cos((float)j * ((float)Math.PI * 2F) / 8.0F) * radius;
                float f9 = (float)j / 8.0F;
                vertexconsumer.vertex(matrix4f, f4, f5, 0.0F).color(rgb.getX(), rgb.getY(), rgb.getZ(), alpha).uv(f6, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
                vertexconsumer.vertex(matrix4f, f4, f5, f1).color(rgb.getX(), rgb.getY(), rgb.getZ(), alpha).uv(f6, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
                vertexconsumer.vertex(matrix4f, f7, f8, f1).color(rgb.getX(), rgb.getY(), rgb.getZ(), alpha).uv(f9, f3).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
                vertexconsumer.vertex(matrix4f, f7, f8, 0.0F).color(rgb.getX(), rgb.getY(), rgb.getZ(), alpha).uv(f9, f2).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(15728880).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
                f4 = f7;
                f5 = f8;
                f6 = f9;
            }

            ms.popPose();
        }
    }


    @Override
    public boolean shouldRender(OpticalSourceBlockEntity p_173531_, Vec3 p_173532_) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    public boolean shouldRendererLaserBeam(OpticalSourceBlockEntity opticalLaserSourceBlockEntity){
        return opticalLaserSourceBlockEntity.getSpeed() != 0 && (opticalLaserSourceBlockEntity.initialBeamProperties != null && opticalLaserSourceBlockEntity.initialBeamProperties.getType().visible()) && !opticalLaserSourceBlockEntity.blockPosToBeamLight.isEmpty();
    }

    @Override
    protected SuperByteBuffer getRotatedModel(OpticalSourceBlockEntity opticalLaserSourceBlockEntity, BlockState state) {
        return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, state
                .getValue(OpticalSourceBlock.HORIZONTAL_FACING)
                .getOpposite());
    }


    private SuperByteBuffer rotateLaser(SuperByteBuffer buffer, float angle, Direction facing) {
        float pivotX = 8F / 16f;
        float pivotY = 8f / 16f;
        float pivotZ = 8F / 16f;
        buffer.rotateCentered(Direction.UP, (float) (AngleHelper.rad(AngleHelper.horizontalAngle(facing.getCounterClockWise())) - 1.5F * Math.PI));
        buffer.translate(pivotX, pivotY, pivotZ);
        buffer.rotate(Direction.NORTH, AngleHelper.rad(angle));
        buffer.translate(-pivotX, -pivotY, -pivotZ);
        return buffer;
    }

}
