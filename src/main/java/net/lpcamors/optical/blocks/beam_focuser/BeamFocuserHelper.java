package net.lpcamors.optical.blocks.beam_focuser;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import net.lpcamors.optical.CORecipeTypes;
import net.lpcamors.optical.blocks.IBeamReceiver;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.recipes.FocusingRecipe;
import net.lpcamors.optical.recipes.FocusingRecipeParams;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Optional;

public class BeamFocuserHelper {

    public static Optional<FocusingRecipe> canBeProcessed(Level world, RecipeWrapper w, BeamHelper.BeamType beamType){

        FocusingRecipeParams.BeamTypeConditionProfile.initializeRecipes(world);

        Optional<FocusingRecipe> focusingRecipe = SequencedAssemblyRecipe.getRecipe(world, w.getItem(0), CORecipeTypes.FOCUSING.getType() ,FocusingRecipe.class);
        if(focusingRecipe.isEmpty()){
            focusingRecipe = world.getRecipeManager().getRecipeFor(CORecipeTypes.FOCUSING.getType(), w, world);
        }
        if(focusingRecipe.isEmpty()) {
            focusingRecipe = FocusingRecipeParams.BeamTypeConditionProfile.getRecipeFor(world, w, beamType);
        }
        if(focusingRecipe.isPresent()){
            if(focusingRecipe.get().beamTypeCondition.test(beamType)){
                return focusingRecipe;
            }
        }
        return Optional.empty();
    }


    public static Optional<FocusingRecipe> hasRequired(Level world, RecipeWrapper w, BeamHelper.BeamType beamType, FilteringBehaviour b){
        Optional<FocusingRecipe> focusingRecipe = SequencedAssemblyRecipe.getRecipe(world, w.getItem(0), CORecipeTypes.FOCUSING.getType() ,FocusingRecipe.class);
        if (focusingRecipe.isEmpty()) {
            focusingRecipe = world.getRecipeManager().getRecipeFor(CORecipeTypes.FOCUSING.getType(), w, world);
        }

        if(focusingRecipe.isEmpty()){
            FocusingRecipeParams.BeamTypeConditionProfile.getRecipeFor(world, w, beamType);
        }

        return focusingRecipe;


    }
    public static ItemStack getResult(Level world, ItemStack stack, IBeamReceiver.BeamSourceInstance beamSourceInstance){
        RecipeWrapper w = new RecipeWrapper(new ItemStackHandler(1));
        w.setItem(0, stack);
        FocusingRecipe focusingRecipe = SequencedAssemblyRecipe.getRecipe(world, stack, CORecipeTypes.FOCUSING.getType(),
                FocusingRecipe.class).filter(BeamFocuserHelper::test).orElseGet(() -> {
                    for(Recipe<RecipeWrapper> recipe : world.getRecipeManager().getRecipesFor(CORecipeTypes.FOCUSING.getType(), w, world)){
                        FocusingRecipe fr = (FocusingRecipe)recipe;
                        if(test(fr)){
                            return fr;
                        }
                    }
                    return null;
        });

         if(focusingRecipe == null){
             Optional<FocusingRecipe> op = FocusingRecipeParams.BeamTypeConditionProfile.getRecipeFor(world, w, beamSourceInstance.optionalBeamProperties().get().beamType);
             if(op.isPresent()) focusingRecipe = op.get();
//            if(beamSourceInstance.optionalBeamProperties().isPresent()){
//                FocusingRecipeParams.BeamTypeCondition bc = FocusingRecipeParams.BeamTypeCondition.getFromType(beamSourceInstance.optionalBeamProperties().get().beamType);
//                ItemStack stack1 = bc.getResult(stack, world);
//                stack.shrink(1);
//                return stack1;
//            }
        }


        if (focusingRecipe != null) {
            List<ItemStack> results = focusingRecipe.rollResults();
            stack.shrink(1);
            return results.isEmpty() ? ItemStack.EMPTY : results.get(0);
        }
        return ItemStack.EMPTY;
        //return GenericItemFilling.fillItem(world, requiredAmount, stack, availableFluid);
    }


    public static boolean test(FocusingRecipe focusingRecipe){
        return true;
    }

}
