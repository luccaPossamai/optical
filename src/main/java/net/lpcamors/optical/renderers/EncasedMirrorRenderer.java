package net.lpcamors.optical.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.fluids.pipes.valve.FluidValveRenderer;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.lpcamors.optical.COPartialModels;
import net.lpcamors.optical.blocks.encased_mirror.EncasedMirrorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;


public class EncasedMirrorRenderer extends ShaftRenderer<EncasedMirrorBlockEntity> {

    public EncasedMirrorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }


    @Override
    protected void renderSafe(EncasedMirrorBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource bufferSource, int light, int overlay) {

        super.renderSafe(be, partialTicks, ms, bufferSource, light, overlay);
        BlockState state = be.getBlockState();
        SuperByteBuffer mirror = CachedBufferer.partial(COPartialModels.MIRROR, state);

        Direction direction = be.getBlockState().getValue(DirectionalKineticBlock.FACING);
        if(direction.getAxis().isHorizontal()){
            mirror.rotateCentered(direction.getClockWise(), (float) (Math.PI / 2F));
        }
        kineticRotationTransform(mirror, be, Direction.Axis.Y, AngleHelper.rad(be.getIndependentAngle(partialTicks)),
                light).light(light).renderInto(ms, bufferSource.getBuffer(RenderType.solid()));

//        mirror.centre()
//                .rotateY(AngleHelper.horizontalAngle(direction))
//                .rotateX(direction == Direction.UP ? 0 : direction == Direction.DOWN ? 180 : 90)
//                .rotateY(0 + pointerRotation)
//                .unCentre()
//                .light(light)
//                .renderInto(ms, bufferSource.getBuffer(RenderType.solid()));
    }


}
