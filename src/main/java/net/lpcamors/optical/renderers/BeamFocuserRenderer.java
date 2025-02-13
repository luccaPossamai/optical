package net.lpcamors.optical.renderers;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AngleHelper;
import com.simibubi.create.foundation.utility.Color;
import net.lpcamors.optical.COPartialModels;
import net.lpcamors.optical.blocks.beam_focuser.BeamFocuserBlockEntity;
import net.lpcamors.optical.blocks.optical_source.OpticalSourceBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class BeamFocuserRenderer extends ShaftRenderer<BeamFocuserBlockEntity> {




    public BeamFocuserRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    @Override
    protected void renderSafe(BeamFocuserBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        BlockState blockState = be.getBlockState();
        Direction direction = blockState.getValue(OpticalSourceBlock.HORIZONTAL_FACING);
        ms.pushPose();
        FilteringRenderer.renderOnBlockEntity(be, partialTicks, ms, buffer, light, overlay);
        ms.translate(0, -0.37,0);
        ms.scale(1, 1.1F, 1F);


        double k = 0.08;
        double radius = 18;
        double alpha = 1.25;
        if(be.processingTicks >= 5 && be.processingTicks <= be.getProcessDuration()){
            double angle = be.getAngle(partialTicks, radius, k, alpha);
            VertexConsumer vb = buffer.getBuffer(RenderType.translucentNoCrumbling());
            SuperByteBuffer focusBeam = CachedBufferer.partial(COPartialModels.FOCUS_BEAM, blockState)
                    .disableDiffuse()

                    .light(LightTexture.FULL_BRIGHT);

            VertexConsumer vb1 = buffer.getBuffer(getBeamRenderType());
            SuperByteBuffer focusBeam1 = CachedBufferer.partial(COPartialModels.FOCUS_BEAM_GLOW, blockState)
                    .light(LightTexture.FULL_BRIGHT)
                    .disableDiffuse();


            Direction.Axis rotDirection = direction.getStepX() == 0 ? direction.getClockWise().getAxis() : direction.getAxis();
            float rot_off = AngleHelper.rad(90 - direction.toYRot());
            kineticRotationTransform(focusBeam, be, Direction.Axis.Y, rot_off, light);
            kineticRotationTransform(focusBeam1, be, Direction.Axis.Y, rot_off, light);

            kineticRotationTransform(focusBeam, be, rotDirection, AngleHelper.rad(angle), light);//.renderInto(ms, vb);
            kineticRotationTransform(focusBeam1, be, rotDirection, AngleHelper.rad(angle), light);//.renderInto(ms, vb1);
            be.getBeamSourceInstance().optionalBeamProperties().ifPresent(beamProperties -> {
                focusBeam.color(beamProperties.color.getX(), beamProperties.color.getY(),beamProperties.color.getZ(), 255).light(15728880).renderInto(ms, vb);
                focusBeam1.color(beamProperties.color.getX(), beamProperties.color.getY(),beamProperties.color.getZ(), 255).light(15728880).renderInto(ms, vb1);
            });
        }
        ms.popPose();
    }

    private SuperByteBuffer rotateLaser(SuperByteBuffer buffer, double angle, Direction facing) {
        float pivotX = 8F / 16f;
        float pivotY = 8f / 16f;
        float pivotZ = 8F / 16f;
        buffer.rotateCentered(Direction.UP, (float) (AngleHelper.rad(AngleHelper.horizontalAngle(facing.getCounterClockWise())) - 1.F * Math.PI));
        buffer.translate(pivotX, pivotY, pivotZ);
        buffer.rotate(facing, AngleHelper.rad(angle));
        buffer.translate(-pivotX, -pivotY, -pivotZ);
        return buffer;
    }

    public RenderType getBeamRenderType(){
        RenderType renderType = RenderType.create("create_optical:laser", DefaultVertexFormat.BLOCK,
                VertexFormat.Mode.QUADS, 256, true, true, RenderType.CompositeState.builder()
                        .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, true))
                        .setDepthTestState(new RenderStateShard.DepthTestStateShard("<=", 515))
                        .setCullState(new RenderStateShard.CullStateShard(false))
                        .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, true))
                        .setTransparencyState(new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
                            RenderSystem.enableBlend();
                            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                        }, () -> {
                            RenderSystem.disableBlend();
                            RenderSystem.defaultBlendFunc();
                        }))
                        .setOutputState(new RenderStateShard.OutputStateShard("translucent_var", () -> {
                            if (Minecraft.useShaderTransparency()) {
                                Minecraft.getInstance().levelRenderer.getTranslucentTarget().bindWrite(false);
                            }
                        }, () -> {
                            if (Minecraft.useShaderTransparency()) {
                                Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
                            }
                        }))
                        .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                        .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeTranslucentShader))
                        .createCompositeState(false));
        return renderType;
    }

}
