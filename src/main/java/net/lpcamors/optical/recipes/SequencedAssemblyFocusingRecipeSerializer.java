package net.lpcamors.optical.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class SequencedAssemblyFocusingRecipeSerializer extends ProcessingRecipeSerializer<FocusingRecipe>  {

    private static final String INGREDIENT_KEY = "input";
    private static final String REQUIRED_BEAM_TYPE_KEY = "required_beam_type";




    public SequencedAssemblyFocusingRecipeSerializer(ProcessingRecipeBuilder.ProcessingRecipeFactory<FocusingRecipe> factory) {
        super(factory);
    }


    protected Ingredient readIngredient(JsonObject json) {
        if (json.has(INGREDIENT_KEY)) {
            return Ingredient.fromJson(json.get(INGREDIENT_KEY).getAsJsonObject());
        }
        return Ingredient.EMPTY;
    }
    protected ProcessingOutput readOutput(JsonObject json) {
        for (JsonElement je : GsonHelper.getAsJsonArray(json, "results")) {
            return ProcessingOutput.deserialize(je);
        }
        return ProcessingOutput.EMPTY;

    }
    protected FocusingRecipeParams.BeamTypeCondition readRequiredBeamType(JsonObject jsonObject){
        if(jsonObject.has(REQUIRED_BEAM_TYPE_KEY)){
            var r = jsonObject.get(REQUIRED_BEAM_TYPE_KEY);
            boolean f = r.isJsonPrimitive() && ((JsonPrimitive) r).isNumber();
            if(r instanceof JsonPrimitive j && j.isNumber() && (j.getAsInt() >= 0 && j.getAsInt() < FocusingRecipeParams.BeamTypeCondition.values().length)){
                return FocusingRecipeParams.BeamTypeCondition.values()[r.getAsInt()];
            }
        }
        return FocusingRecipeParams.BeamTypeCondition.NONE;
    }
    protected FocusingRecipeParams.BeamTypeCondition readRequiredBeamType(FriendlyByteBuf buffer){
        int i = buffer.readInt();
        if(i >= 0 && i < FocusingRecipeParams.BeamTypeCondition.values().length){
            return FocusingRecipeParams.BeamTypeCondition.values()[i];
        }
        return FocusingRecipeParams.BeamTypeCondition.NONE;
    }


    @Override
    protected void writeToJson(JsonObject json, FocusingRecipe recipe) {
        super.writeToJson(json, recipe);
        json.addProperty(REQUIRED_BEAM_TYPE_KEY,recipe.getRequiredBeamType().getId());
    }

    @Override
    protected void writeToBuffer(@NotNull FriendlyByteBuf buffer, FocusingRecipe recipe) {
        recipe.getIngredient().toNetwork(buffer);
        recipe.getOutput().write(buffer);
        buffer.writeInt(recipe.getRequiredBeamType().getId());
    }


    @Override
    protected @NotNull FocusingRecipe readFromBuffer(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
        return new FocusingRecipe(new FocusingRecipeParams(Ingredient.fromNetwork(buffer), ProcessingOutput.read(buffer), recipeId));
    }

    @Override
    protected @NotNull FocusingRecipe readFromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
        return new FocusingRecipe(new FocusingRecipeParams(readIngredient(json), readOutput(json), recipeId));
    }
}
