package net.lpcamors.optical.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.COPartialModels;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.IBeamSource;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.blocks.optical_source.OpticalSourceBlock;
import net.lpcamors.optical.blocks.optical_source.OpticalSourceBlockEntity;
import net.lpcamors.optical.blocks.optical_source.OpticalSourceBlockEntityVar;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class OpticalSourceRenderer extends KineticBlockEntityRenderer<OpticalSourceBlockEntityVar> {
    public static final ResourceLocation LASER_BEAM_LOCATION = new ResourceLocation(COMod.ID, "textures/block/optical_source/optical_source_laser_beam.png");

    private static final RenderType LASER_BEAM_RENDER_TYPE = RenderType.entityTranslucentEmissive(LASER_BEAM_LOCATION, true);
    public OpticalSourceRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(OpticalSourceBlockEntityVar p_112306_) {
        return true;
    }

    @Override
    protected void renderSafe(OpticalSourceBlockEntityVar opticalLaserSourceBlockEntity, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(opticalLaserSourceBlockEntity, partialTicks, ms, buffer, light, overlay);
        BlockState blockState = opticalLaserSourceBlockEntity.getBlockState();
        Direction direction = blockState.getValue(OpticalSourceBlock.HORIZONTAL_FACING);

        VertexConsumer vb = buffer.getBuffer(RenderType.solid());
        SuperByteBuffer laser = CachedBufferer.partial(COPartialModels.LASER, blockState);

        float time = AnimationTickHolder.getRenderTime(opticalLaserSourceBlockEntity.getLevel());
        float speed = opticalLaserSourceBlockEntity.getSpeed() * 2;
        float angle = (time * speed * 3 / 10f) % 360;
        angle = 0F; // Just to fix it, without really implement the user desire
        rotateLaser(laser, angle, direction).light(light).renderInto(ms, vb);
        if(opticalLaserSourceBlockEntity.shouldRendererLaserBeam()){
            IBeamSource.ClientSide.renderLaserBeam(opticalLaserSourceBlockEntity, partialTicks, ms, buffer, light);
        }
    }


    @Override
    public boolean shouldRender(OpticalSourceBlockEntityVar p_173568_, Vec3 p_173569_) {
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
    protected SuperByteBuffer getRotatedModel(OpticalSourceBlockEntityVar opticalLaserSourceBlockEntity, BlockState state) {
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
