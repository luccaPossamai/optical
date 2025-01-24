package net.lpcamors.optical.recipes;

import com.google.gson.JsonObject;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import net.lpcamors.optical.CORecipeTypes;
import net.lpcamors.optical.blocks.COBlocks;
import net.lpcamors.optical.blocks.beam_focuser.BeamFocuserBlockEntity;
import net.lpcamors.optical.compat.jei.FocusingAssemblySubcategory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class FocusingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {


    private static final String REQUIRED_BEAM_TYPE_KEY = "required_beam_type";

    public FocusingRecipeParams.BeamTypeCondition beamTypeCondition = FocusingRecipeParams.BeamTypeCondition.NONE;

    public static FocusingRecipe radio(ProcessingRecipeBuilder.ProcessingRecipeParams params){
        FocusingRecipe f = new FocusingRecipe(params);
        f.beamTypeCondition = FocusingRecipeParams.BeamTypeCondition.RADIO;
        return f;
    }
    public static FocusingRecipe microwave(ProcessingRecipeBuilder.ProcessingRecipeParams params){
        FocusingRecipe f = new FocusingRecipe(params);
        f.beamTypeCondition = FocusingRecipeParams.BeamTypeCondition.MICROWAVE;
        return f;
    }
    public static FocusingRecipe visible(ProcessingRecipeBuilder.ProcessingRecipeParams params){
        FocusingRecipe f = new FocusingRecipe(params);
        f.beamTypeCondition = FocusingRecipeParams.BeamTypeCondition.VISIBLE;
        return f;
    }
    public static FocusingRecipe gamma(ProcessingRecipeBuilder.ProcessingRecipeParams params){
        FocusingRecipe f = new FocusingRecipe(params);
        f.beamTypeCondition = FocusingRecipeParams.BeamTypeCondition.GAMMA;
        return f;
    }
    public static FocusingRecipe none(ProcessingRecipeBuilder.ProcessingRecipeParams params){
        FocusingRecipe f = new FocusingRecipe(params);
        f.beamTypeCondition = FocusingRecipeParams.BeamTypeCondition.NONE;
        return f;
    }

    public FocusingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(CORecipeTypes.FOCUSING, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 2;
    }

    @Override
    protected int getMaxOutputCount() {
        return 2;
    }

    @Override
    public Component getDescriptionForAssembly() {
        return Component.translatable("recipe.create_optical.focusing.sequence");
    }

    @Override
    public void addRequiredMachines(Set<ItemLike> list) {
        list.add(COBlocks.BEAM_FOCUSER.get());
    }

    @Override
    public void addAssemblyIngredients(List<Ingredient> list) {
        if(this.ingredients.size() > 1)
            list.add(this.getIngredients().get(1));
    }

    @Override
    public Supplier<Supplier<SequencedAssemblySubCategory>> getJEISubCategory() {
        return () -> FocusingAssemblySubcategory::new;
    }
    @Override
    public boolean matches(RecipeWrapper p_44002_, Level p_44003_) {
        if(p_44002_.isEmpty()) return false;
        boolean f = this.getIngredient().test(p_44002_.getItem(0));
        if(p_44002_.getContainerSize() > 1){
            f &= this.getSecondIngredient().test(p_44002_.getItem(1));
        }
        return f;
    }



    public Ingredient getIngredient() {
        return this.getIngredients().get(0);
    }
    public Ingredient getSecondIngredient() {
        if(this.ingredients.size() > 1){
            return this.getIngredients().get(1);
        }
        return Ingredient.EMPTY;
    }

    public ProcessingOutput getOutput(){
        return this.results.get(0);
    }

    @Override
    public int getProcessingDuration() {
        int i = super.getProcessingDuration();
        return i == 0 ? BeamFocuserBlockEntity.PROCESSING_TICK : i;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    public FocusingRecipeParams.BeamTypeCondition getRequiredBeamType() {
        return this.beamTypeCondition;
    }

    protected FocusingRecipeParams.BeamTypeCondition readRequiredBeamType(JsonObject jsonObject) {
        if (jsonObject.has(REQUIRED_BEAM_TYPE_KEY)) {
            var got = jsonObject.get(REQUIRED_BEAM_TYPE_KEY);
            var parsed = FocusingRecipeParams.BeamTypeCondition.INDEXED.fromJson(got);
            if (parsed != null) {
                return parsed;
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
    public void writeAdditional(JsonObject json) {
        super.writeAdditional(json);
        json.addProperty(REQUIRED_BEAM_TYPE_KEY, this.getRequiredBeamType().getId());
    }

    @Override
    public void writeAdditional(FriendlyByteBuf buffer) {
        super.writeAdditional(buffer);
        buffer.writeInt(this.getRequiredBeamType().getId());
    }

    @Override
    public void readAdditional(JsonObject json) {
        super.readAdditional(json);
        this.beamTypeCondition = readRequiredBeamType(json);
    }

    @Override
    public void readAdditional(FriendlyByteBuf buffer) {
        super.readAdditional(buffer);
        this.beamTypeCondition = readRequiredBeamType(buffer);
    }
}
