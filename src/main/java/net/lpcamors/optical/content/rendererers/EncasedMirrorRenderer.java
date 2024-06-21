package net.lpcamors.optical.content.rendererers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.lpcamors.optical.COPartialModels;
import net.lpcamors.optical.content.blocks.mirror.EncasedMirrorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;


public class EncasedMirrorRenderer extends ShaftRenderer<EncasedMirrorBlockEntity> {

    public EncasedMirrorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    protected void renderSafe(EncasedMirrorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, bufferSource, light, overlay);
        BlockState state = be.getBlockState();
        SuperByteBuffer filter = CachedBufferer.partial(COPartialModels.MIRROR, state);

        Direction direction = be.getBlockState().getValue(DirectionalKineticBlock.FACING);
        if(direction.getAxis().isHorizontal()){
            filter.rotateCentered(direction.getClockWise(), (float) (Math.PI / 2F));
        }

        kineticRotationTransform(filter, be, Direction.Axis.Y, be.getIndependentAngle(partialTicks),
                light).renderInto(ms, bufferSource.getBuffer(RenderType.solid()));
    }


}
