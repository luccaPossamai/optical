package net.lpcamors.optical.content.rendererers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.RenderTypes;
import net.lpcamors.optical.COPartialModels;
import net.lpcamors.optical.content.blocks.optical_sensor.OpticalSensorBlock;
import net.lpcamors.optical.content.blocks.optical_sensor.OpticalSensorBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class OpticalSensorRenderer extends SafeBlockEntityRenderer<OpticalSensorBlockEntity> {

    public OpticalSensorRenderer(BlockEntityRendererProvider.Context context) {
    }


    @Override
    protected void renderSafe(OpticalSensorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {

        BlockState blockState = be.getBlockState();

        boolean f = blockState.getValue(OpticalSensorBlock.INTENSITY) > 0;

        if(!f) return;

        ms.pushPose();

        CachedBufferer.partial(COPartialModels.OPTICAL_SENSOR_LAMP, blockState)
                .light(LightTexture.FULL_BRIGHT)
                .renderInto(ms, bufferSource.getBuffer(RenderType.translucent()));

        CachedBufferer.partial(COPartialModels.OPTICAL_SENSOR_LAMP_GLOW, blockState)
                .light(LightTexture.FULL_BRIGHT)
                .color(100, 100, 100, 255)
                .disableDiffuse()
                .renderInto(ms, bufferSource.getBuffer(RenderTypes.getAdditive()));

        ms.popPose();
    }
}
