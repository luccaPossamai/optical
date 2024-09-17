package net.lpcamors.optical.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.IAssemblyRecipe;
import net.lpcamors.optical.CORecipeTypes;
import net.lpcamors.optical.blocks.COBlocks;
import net.lpcamors.optical.blocks.beam_focuser.BeamFocuserBlock;
import net.lpcamors.optical.blocks.beam_focuser.BeamFocuserBlockEntity;
import net.lpcamors.optical.compat.jei.FocusingAssemblySubcategory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class FocusingRecipe extends ProcessingRecipe<RecipeWrapper> implements IAssemblyRecipe {


    private static final String REQUIRED_BEAM_TYPE_KEY = "required_beam_type";


    //*
    public FocusingRecipeParams.BeamTypeCondition beamTypeCondition = FocusingRecipeParams.BeamTypeCondition.NONE;
//    public static FocusingRecipeInfo INFO_RADIO = new FocusingRecipeInfo("radio", (SequencedAssemblyFocusingRecipeSerializer) CORecipeTypes.RADIO_SERIALIZER.get(), CORecipeTypes.FOCUSING.get());
//    public static FocusingRecipeInfo INFO_MICROWAVE = new FocusingRecipeInfo("microwave", (SequencedAssemblyFocusingRecipeSerializer) CORecipeTypes.MICROWAVE_SERIALIZER.get(), CORecipeTypes.FOCUSING.get());
//    public static FocusingRecipeInfo INFO_VISIBLE = new FocusingRecipeInfo("visible", (SequencedAssemblyFocusingRecipeSerializer) CORecipeTypes.VISIBLE_SERIALIZER.get(), CORecipeTypes.FOCUSING.get());
//    public static FocusingRecipeInfo INFO_GAMMA = new FocusingRecipeInfo("visible", (SequencedAssemblyFocusingRecipeSerializer) CORecipeTypes.GAMMA_SERIALIZER.get(), CORecipeTypes.FOCUSING.get());
//    public static FocusingRecipeInfo INFO_NONE = new FocusingRecipeInfo("none", (SequencedAssemblyFocusingRecipeSerializer) CORecipeTypes.NONE_SERIALIZER.get(), CORecipeTypes.FOCUSING.get());
//
//    public static Map<FocusingRecipeParams.BeamTypeCondition, FocusingRecipeInfo> MAP_INFO = Map.of(
//            FocusingRecipeParams.BeamTypeCondition.RADIO, INFO_RADIO,
//            FocusingRecipeParams.BeamTypeCondition.MICROWAVE, INFO_MICROWAVE,
//            FocusingRecipeParams.BeamTypeCondition.VISIBLE, INFO_VISIBLE,
//            FocusingRecipeParams.BeamTypeCondition.GAMMA, INFO_GAMMA,
//            FocusingRecipeParams.BeamTypeCondition.NONE, INFO_NONE
//    );
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

    public FocusingRecipeParams.BeamTypeCondition getRequiredBeamType() {
        return this.beamTypeCondition;
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
