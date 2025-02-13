package net.lpcamors.optical.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.clock.CuckooClockBlockEntity;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.RenderTypes;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import net.lpcamors.optical.COPartialModels;
import net.lpcamors.optical.COUtils;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.IBeamSource;
import net.lpcamors.optical.blocks.absorption_polarizing_filter.AbsorptionPolarizingFilter;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.blocks.optical_source.OpticalSourceBlock;
import net.lpcamors.optical.blocks.optical_source.OpticalSourceBlockEntity;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class OpticalSourceRenderer extends KineticBlockEntityRenderer<OpticalSourceBlockEntity> {

     public OpticalSourceRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRenderOffScreen(OpticalSourceBlockEntity p_112306_) {
        return true;
    }

    @Override
    protected void renderSafe(OpticalSourceBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);


        if(be.shouldRendererLaserBeam()) {
            IBeamSource.ClientSide.renderLaserBeam(be, be.getBlockState(), ms, buffer);
            /*
            Vec3 pos = be.getBlockPos().getCenter();
            //IBeamSource.ClientSide.renderLaserBeam(opticalLaserSourceBlockEntity, partialTicks, ms, buffer, light);
            BlockState state = be.getBlockState();
            List<Pair<Vec3i, Vec3i>> blockPosToBeam = be.getBeamPropertiesMap().keySet().stream().toList();

            Direction direction = state.getValue(AbsorptionPolarizingFilter.FACING);

            for (int i = 0; i < blockPosToBeam.size() ; i++) {

                Pair<Vec3i, Vec3i> pair = blockPosToBeam.get(i);

                BeamHelper.BeamProperties beamProperties = be.getBeamPropertiesMap().get(pair);
                direction = beamProperties.direction;
                Vec3 start0 = Vec3.atCenterOf(pair.getFirst());
                Vec3 end0 = Vec3.atCenterOf(pair.getSecond());

                Vec3 start = start0.subtract(IBeamReceiver.getLaserIrradiatedFaceOffset(beamProperties.direction, new BlockPos(pair.getFirst()), be.getLevel()));
                Vec3 end = end0.add(IBeamReceiver.getLaserIrradiatedFaceOffset(beamProperties.direction, new BlockPos(pair.getSecond()), be.getLevel()));

                ms.pushPose();

                translateForVec(ms, start0.subtract(pos));
                translateForVec(ms, start.subtract(start0));
                translateForVec(ms, end.subtract(start).multiply(0.5D, 0.5D, 0.5D));
                //Vec3 vec2 = end.subtract(start);
                //ms.translate(vec2.x / 2, vec3.y / 2,vec3.z / 2);


                float f = (float) end.subtract(start).length();
                float f1 = (float) end.subtract(start).length();

                SuperByteBuffer laser = CachedBufferer.partial(COPartialModels.LASER_BEAM, state)
                        .light(LightTexture.FULL_BRIGHT)
                        .disableDiffuse();


                Vec3 dir = direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ?
                        Vec3.atLowerCornerOf(direction.getNormal()) :
                        Vec3.atLowerCornerOf(direction.getNormal()).scale(-1) ;
                Vec3 nDir = new Vec3(1,1,1).subtract(dir);

                Vec3i color = COUtils.getColor(beamProperties.dyeColor);
                for(int j = 0; j < 3 + Math.floor(beamProperties.intensity / 16); j ++){
                    SuperByteBuffer laser0 = laser;
                    double radius = 0.8 + j * 0.2;
                    int alpha = (int) (255 * (1 - j / 10F));
                    laser0.color(color.getX(), color.getY(), color.getZ(), alpha);
                    scaleForVec(laser0, dir.scale(f).add(nDir));
                    scaleForVec(laser0, nDir.scale(radius).add(dir));
                    rotateDirection(laser0, direction);
                    laser0.renderInto(ms, j == 0 ? buffer.getBuffer(RenderTypes.getAdditive()): buffer.getBuffer(RenderType.translucentNoCrumbling()));
                }
                ms.popPose();
            }

             */
        }
    }

    @Override
    public boolean shouldRender(OpticalSourceBlockEntity p_173568_, Vec3 p_173569_) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }



    @Override
    protected SuperByteBuffer getRotatedModel(OpticalSourceBlockEntity opticalLaserSourceBlockEntity, BlockState state) {
        return CachedBufferer.partialFacing(AllPartialModels.SHAFT_HALF, state, state
                .getValue(OpticalSourceBlock.HORIZONTAL_FACING)
                .getOpposite());
    }

}
