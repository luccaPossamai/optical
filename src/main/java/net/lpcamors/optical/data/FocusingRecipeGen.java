package net.lpcamors.optical.data;

import com.simibubi.create.compat.jei.category.BasinCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.CORecipeTypes;
import net.lpcamors.optical.compat.jei.FocusingRecipeBuilder;
import net.lpcamors.optical.items.COItems;
import net.lpcamors.optical.recipes.FocusingRecipe;
import net.lpcamors.optical.recipes.FocusingRecipeParams;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;


public class FocusingRecipeGen extends ProcessingRecipeGen {

    GeneratedRecipe
            MIRROR = create(() -> COMod.loc("mirror"), f -> {
                return (FocusingRecipeBuilder) f.require(Tags.Items.GLASS_PANES).output(COItems.MIRROR).duration(50);
            }, FocusingRecipeParams.BeamTypeCondition.VISIBLE),
            FILTER = create(() -> COMod.loc("filter"), f -> {
                return (FocusingRecipeBuilder) f.require(Items.TINTED_GLASS).output(COItems.POLARIZING_FILTER).duration(50);
            }, FocusingRecipeParams.BeamTypeCondition.VISIBLE);


    public FocusingRecipeGen(PackOutput generator) {
        super(generator);
    }


    protected <T extends ProcessingRecipe<?>> GeneratedRecipe create(Supplier<ResourceLocation> name,
                                                                     UnaryOperator<FocusingRecipeBuilder> transform,
                                                                     FocusingRecipeParams.BeamTypeCondition b) {
        ProcessingRecipeSerializer<FocusingRecipe> serializer = getSerializer();

        GeneratedRecipe generatedRecipe =
                c -> transform.apply(new FocusingRecipeBuilder(serializer.getFactory(), name.get(), b)).build(c);

        all.add(generatedRecipe);
        return generatedRecipe;
    }

    @Override
    protected IRecipeTypeInfo getRecipeType() {
        return CORecipeTypes.FOCUSING;
    }




}
