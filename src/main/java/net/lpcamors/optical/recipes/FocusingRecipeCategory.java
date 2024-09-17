package net.lpcamors.optical.recipes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.MillingCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.utility.Lang;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

public class FocusingRecipeCategory extends CreateRecipeCategory<FocusingRecipe> {

    private final AnimatedFocus focus = new AnimatedFocus(true);

    public FocusingRecipeCategory(Info<FocusingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FocusingRecipe recipe, IFocusGroup focuses) {
        builder
                .addSlot(RecipeIngredientRole.INPUT, 21, 48)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getIngredients().get(0));
        if(recipe.getIngredients().size() > 1){
            int x = 70;
            int y = 12;
            builder.addSlot(RecipeIngredientRole.CATALYST, x, y)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addIngredients(recipe.getIngredients().get(1));
        }
        List<ProcessingOutput> results = recipe.getRollableResults();
        boolean single = results.size() == 1;
        int i = 0;
        for (ProcessingOutput output : results) {
            int xOffset = i % 2 == 0 ? 0 : 19;
            int yOffset = (i / 2) * -19;

            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, single ? 141 : 135 + xOffset, 48 + yOffset)
                    .setBackground(getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addTooltipCallback(addStochasticTooltip(output));

            i++;
        }
    }

    @Override
    public Component getTitle() {
        return Component.translatable("recipe.create_optical.focusing");
    }

    @Override
    public void draw(FocusingRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        PoseStack ms = graphics.pose();
        ms.pushPose();
        renderWidgets(graphics, recipe, mouseX, mouseY);
        focus.draw(graphics, getWidth() / 2 + 3, 34);
        ms.popPose();
    }

    protected void renderWidgets(GuiGraphics graphics, FocusingRecipe recipe, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SHADOW.render(graphics, 81, 68);
        int vRows = (1 + recipe.getFluidResults().size() + recipe.getRollableResults().size()) / 2;

        if (vRows <= 2)
            AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 136, -19 * (vRows - 1) + 32);
        AllGuiTextures heatBar = AllGuiTextures.JEI_NO_HEAT_BAR;
        heatBar.render(graphics, 4, 80);
        graphics.drawString(Minecraft.getInstance().font, Lang.translateDirect(recipe.getRequiredBeamType().getTranslationKey()), 9,
                86, recipe.getRequiredBeamType().getColor(), false);
    }


}
