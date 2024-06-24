package net.lpcamors.optical.content.rendererers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.lpcamors.optical.COPartialModels;
import net.lpcamors.optical.content.blocks.polarizing_beam_splitter_block.PolarizingBeamSplitterBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class PolarizingBeamSplitterRenderer extends SafeBlockEntityRenderer<PolarizingBeamSplitterBlockEntity> {
    public PolarizingBeamSplitterRenderer(BlockEntityRendererProvider.Context context) {
    }
    @Override
    protected void renderSafe(PolarizingBeamSplitterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        BlockState state = be.getBlockState();
        SuperByteBuffer cube = CachedBufferer.partial(COPartialModels.POLARIZING_BEAM_SPLITTER, state).light(light);
        cube.renderInto(ms, bufferSource.getBuffer(RenderType.translucent()));
    }
}
