package net.lpcamors.optical.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.lpcamors.optical.blocks.IBeamSource;
import net.lpcamors.optical.blocks.beam_condenser.BeamCondenserBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class BeamCondenserRenderer extends SafeBlockEntityRenderer<BeamCondenserBlockEntity> {


    public BeamCondenserRenderer(BlockEntityRendererProvider.Context context){}

    @Override
    protected void renderSafe(BeamCondenserBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        if(be.shouldRendererLaserBeam()){
            IBeamSource.ClientSide.renderLaserBeam(be, be.getBlockState(), ms, bufferSource);
        }
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
