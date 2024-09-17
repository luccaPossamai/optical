package net.lpcamors.optical.recipes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.foundation.gui.element.GuiGameElement;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import net.lpcamors.optical.COPartialModels;
import net.lpcamors.optical.blocks.COBlocks;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public class AnimatedFocus extends AnimatedKinetics {



    private int tick = 0;
    private final boolean depot;
    public AnimatedFocus(boolean depot){
        this.depot = depot;
    }


    @Override
    public void draw(@NotNull GuiGraphics graphics, int xOffset, int yOffset) {
        tick += 1;
        float tic = AnimationTickHolder.getRenderTime();
        double angle = getAngleByTick(tic, 18, 0.08, 1.2);
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 200);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f));
        int scale = 24;

        blockElement(shaft(Direction.Axis.Z))
                .rotateBlock(0, 0, getCurrentAngle())
                .scale(scale)
                .render(graphics);

        GuiGameElement.GuiRenderBuilder r = GuiGameElement.of(COPartialModels.FOCUS_BEAM_UI)
                .scale(scale)
                .rotateBlock(0, 90, angle)
                .atLocal(0, 0.3, 0)
                .withAlpha(0.3F);
        r.render(graphics);


        blockElement(COBlocks.BEAM_FOCUSER.getDefaultState())

                .scale(scale)
                .render(graphics);
        if(depot){
            blockElement(AllBlocks.DEPOT.getDefaultState())
                    .atLocal(0, 1.65, 0)
                    .scale(scale)
                    .render(graphics);
        }

        matrixStack.popPose();
    }

    //Use alpha = (0, pi/2]
    //alpha = 0, diverge, alpha -> you get basic cos sin behaviour
    public static double getAngleByTick(float tick, double radius, double angFreq, double alpha){
        double x = Math.cos(angFreq * tick);
        return radius * Math.tan(x * alpha) / Math.tan(alpha);
    }

}
