package net.lpcamors.optical.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedRecipe;
import com.simibubi.create.foundation.utility.Lang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.lpcamors.optical.recipes.AnimatedFocus;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;

public class FocusingAssemblySubcategory extends SequencedAssemblySubCategory {

    private final AnimatedFocus focus = new AnimatedFocus(false);


    public FocusingAssemblySubcategory() {
        super(20);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SequencedRecipe<?> recipe, IFocusGroup focuses, int x) {
        IRecipeSlotBuilder slot = builder
                .addSlot(RecipeIngredientRole.CATALYST, x + 4, 15)
                .setBackground(CreateRecipeCategory.getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getRecipe().getIngredients().get(1));

    }


    @Override
    public void draw(SequencedRecipe<?> recipe, GuiGraphics graphics, double mouseX, double mouseY, int index) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        ms.translate(-5, 50f, 0);
        ms.scale(.6f, .6f, .6f);
        focus.draw(graphics, getWidth() / 2, 0);
        ms.popPose();
    }

}
