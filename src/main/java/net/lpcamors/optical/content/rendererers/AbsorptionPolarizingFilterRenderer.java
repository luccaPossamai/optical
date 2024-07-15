package net.lpcamors.optical.content.rendererers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.lpcamors.optical.COPartialModels;
import net.lpcamors.optical.content.blocks.absorption_polarizing_filter.AbsorptionPolarizingFilter;
import net.lpcamors.optical.content.blocks.absorption_polarizing_filter.AbsorptionPolarizingFilterBlockEntity;
import net.lpcamors.optical.content.blocks.optical_source.BeamHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class AbsorptionPolarizingFilterRenderer extends SafeBlockEntityRenderer<AbsorptionPolarizingFilterBlockEntity> {
    public AbsorptionPolarizingFilterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    protected void renderSafe(AbsorptionPolarizingFilterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        BlockState state = be.getBlockState();
        Direction direction = state.getValue(AbsorptionPolarizingFilter.FACING);
        SuperByteBuffer filter = CachedBufferer.partial(COPartialModels.POLARIZING_FILTER, state);
        BeamHelper.BeamPolarization beamPolarization = state.getValue(AbsorptionPolarizingFilter.POLARIZATION);
        if(beamPolarization != BeamHelper.BeamPolarization.RANDOM){
            rotateFilter(filter, (float) (beamPolarization.getAngle() * Math.PI / 4), direction).light(light).renderInto(ms, bufferSource.getBuffer(RenderType.translucentNoCrumbling()));
        }


    }

    private SuperByteBuffer rotateFilter(SuperByteBuffer buffer, float angleRad, Direction facing) {
        float pivotX = 8F / 16f;
        float pivotY = 8f / 16f;
        float pivotZ = 8F / 16f;
        buffer.rotateCentered(Direction.UP, (float) (AngleHelper.rad(AngleHelper.horizontalAngle(facing.getCounterClockWise())) - 1.5F * Math.PI));
        buffer.translate(pivotX, pivotY, pivotZ);
        buffer.rotate(Direction.EAST, angleRad);
        buffer.translate(-pivotX, -pivotY, -pivotZ);
        return buffer;
    }
}
