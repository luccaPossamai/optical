package net.lpcamors.optical.compat.jei;

import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.lpcamors.optical.recipes.FocusingRecipe;
import net.lpcamors.optical.recipes.FocusingRecipeParams;
import net.minecraft.resources.ResourceLocation;

public class FocusingRecipeBuilder extends ProcessingRecipeBuilder<FocusingRecipe>{

    private final FocusingRecipeParams.BeamTypeCondition beamTypeCondition;

    public FocusingRecipeBuilder(ProcessingRecipeFactory<FocusingRecipe> factory, ResourceLocation recipeId, FocusingRecipeParams.BeamTypeCondition b) {
        super(factory, recipeId);
        this.beamTypeCondition = b;
    }

    @Override
    public FocusingRecipe build() {
        FocusingRecipe r = factory.create(params);
        r.beamTypeCondition = this.beamTypeCondition;
        return r;
    }
}
