package net.lpcamors.optical.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.fluids.drain.ItemDrainBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.lpcamors.optical.blocks.IBeamSource;
import net.lpcamors.optical.blocks.optical_source.OpticalSourceBlock;
import net.lpcamors.optical.blocks.thermal_optical_source.ThermalOpticalSourceBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class ThermalOpticalSourceRenderer extends KineticBlockEntityRenderer<ThermalOpticalSourceBlockEntity> {

    public ThermalOpticalSourceRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull ThermalOpticalSourceBlockEntity be) {
        return true;
    }

    @Override
    protected void renderSafe(ThermalOpticalSourceBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        renderFluid(be, partialTicks, ms, buffer, light);
        if(be.shouldRendererLaserBeam()){
            IBeamSource.ClientSide.renderLaserBeam(be, be.getBlockState(), ms, buffer);
        }
    }

    protected void renderFluid(ThermalOpticalSourceBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer,
                               int light) {
        SmartFluidTankBehaviour tank = be.internalTank;
        if (tank == null)
            return;

        SmartFluidTankBehaviour.TankSegment primaryTank = tank.getPrimaryTank();
        FluidStack fluidStack = primaryTank.getRenderedFluid();
        float level = primaryTank.getFluidLevel()
                .getValue(partialTicks);

        if (!fluidStack.isEmpty() && level != 0) {
            float yMin = 1.01f / 16f;
            float min = 1.01f / 16f;
            float max = 14.99F / 16f;
            float yOffset = yMin + (7.97F / 16f) * level;
            ms.pushPose();
            ms.translate(0, yOffset, 0);
            FluidRenderer.renderFluidBox(fluidStack, min, yMin - yOffset, min, max, yMin, max, buffer, ms, light,
                    false);
            ms.popPose();
        }
    }


    @Override
    public boolean shouldRender(ThermalOpticalSourceBlockEntity be, Vec3 vec) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }


    @Override
    protected SuperByteBuffer getRotatedModel(ThermalOpticalSourceBlockEntity be, BlockState state) {
        return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, state
                .getValue(OpticalSourceBlock.HORIZONTAL_FACING)
                .getOpposite());
    }

}
