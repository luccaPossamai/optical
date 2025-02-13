package net.lpcamors.optical.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.fluids.drain.ItemDrainBlock;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.lpcamors.optical.COPartialModels;
import net.lpcamors.optical.blocks.optical_receptor.OpticalReceptorBlockEntity;
import net.lpcamors.optical.blocks.polarizing_beam_splitter_block.PolarizingBeamSplitterBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class OpticalReceptorRenderer extends ShaftRenderer<OpticalReceptorBlockEntity> {
    public OpticalReceptorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(OpticalReceptorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, bufferSource, light, overlay);
        BlockState state = be.getBlockState();
        ms.translate(0, 0, 0);
        Direction direction;
        for(int i = 0; i < be.sensor.size(); i++){

            direction = be.integerDirectionMap.get(i);
            if(be.sensor.get(i).isEmpty() || direction == null) continue;
            SuperByteBuffer cube = CachedBufferer.partial(COPartialModels.OPTICAL_DEVICE_HORIZONTAL, state).centre().light(light);
            cube.rotateToFace(direction);
            cube.renderInto(ms, bufferSource.getBuffer(RenderType.solid()));

        }
    }

}
