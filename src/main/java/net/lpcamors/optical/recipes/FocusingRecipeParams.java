package net.lpcamors.optical.recipes;

import com.google.gson.internal.NonNullElementWrapperList;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.data.FocusingRecipeGen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class FocusingRecipeParams extends ProcessingRecipeBuilder.ProcessingRecipeParams {

    private final ResourceLocation id;

    public FocusingRecipeParams(Ingredient ingredient, ItemStack itemStack, ResourceLocation id) {
        this(ingredient, new ProcessingOutput(itemStack, 1), id);
    }

    public FocusingRecipeParams(Ingredient ingredient, ProcessingOutput processingOutput, ResourceLocation id) {
        this(List.of(ingredient), List.of(processingOutput), id);
    }

    public FocusingRecipeParams(List<Ingredient> ingredient, List<ProcessingOutput> processingOutput, ResourceLocation id) {
        this(id);
        this.ingredients.addAll(ingredient);
        this.results.addAll(processingOutput);
    }

    public FocusingRecipeParams(ResourceLocation id) {
        super(id);
        this.id = id;
    }

    public NonNullList<Ingredient> getIngredients(){
        return this.ingredients;
    }
    public NonNullList<ProcessingOutput> getOutputs(){
        return this.results;
    }
    private static List<ProcessingOutput> append(ProcessingOutput processingOutput, List<ProcessingOutput> p){
        List<ProcessingOutput> p0 = new ArrayList<>();
        p0.add(processingOutput);
        p0.addAll(p);
        return p0;
    }
    static ResourceLocation id(Recipe<?> r, FocusingRecipeParams.BeamTypeCondition beamTypeCondition){
        return new ResourceLocation(COMod.ID, r.getId().getPath() + "_focusing_" +beamTypeCondition.name().toLowerCase());
    }

    public ResourceLocation getId() {
        return id;
    }

    public enum BeamTypeCondition {

        RADIO(0, 6192150),
        MICROWAVE(1,
                (level, itemStack) -> {
            RecipeWrapper recipeWrapper = new RecipeWrapper(new ItemStackHandler(1));
            recipeWrapper.setItem(0, itemStack);
            Optional<SmokingRecipe> recipe = level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMOKING, recipeWrapper, level);
            if (recipe.isPresent())
                return true;
            return false;
            },
                (level, itemStack) -> {
            RecipeWrapper recipeWrapper = new RecipeWrapper(new ItemStackHandler(1));
            recipeWrapper.setItem(0, itemStack);
            Optional<SmokingRecipe> recipe = level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMOKING, recipeWrapper, level);
            return recipe.map(smokingRecipe -> smokingRecipe.getResultItem(level.registryAccess())).orElse(ItemStack.EMPTY);
                }, 8991416),
        VISIBLE(2, 0xE88300),
        GAMMA(3, 0x5C93E8),
        NONE(4, 0xffffff)
        ;

        private final int id;
        private final BiFunction<Level, ItemStack, Boolean> bf;
        private final BiFunction<Level, ItemStack, ItemStack> of;
        private final int color;

        BeamTypeCondition(int id, int color) {
            this(id, (level, itemStack) -> false, (level, itemStack) -> ItemStack.EMPTY, color);
        }
        BeamTypeCondition(int id,  BiFunction<Level, ItemStack, Boolean> bf,
                          BiFunction<Level, ItemStack, ItemStack> of, int color){
            this.id = id;
            this.bf = bf;
            this.of = of;
            this.color = color;
        }

        public int getId() {
            return id;
        }
        public boolean test(BeamHelper.BeamType beamType){
            return this.id == 4 || (beamType.id == this.id);
        }

        public static BeamTypeCondition getFromType(BeamHelper.BeamType b){
            return Arrays.stream(BeamTypeCondition.values()).filter(beamTypeCondition1 -> beamTypeCondition1.test(b) && beamTypeCondition1.id != 4).toList().get(0);
        }

        public boolean canProcessItem(ItemStack stack, Level level){
            return this.bf.apply(level, stack) || NONE.bf.apply(level, stack);
        }

        public ItemStack getResult(ItemStack stack, Level level){
            ItemStack stack1 = this.of.apply(level, stack);
            if(stack1.isEmpty()){
                stack1 = NONE.of.apply(level, stack);
            }
            return stack1;
        }
        public int getColor(){
            return this.color;
        }
        public String getTranslationKey(){
            return "create_optical.recipe.required_beam_type."+this.name().toLowerCase();
        }
    }


    public enum BeamTypeConditionProfile {
        SMOKING(RecipeType.SMOKING, registryAccess -> recipe -> FocusingRecipe.microwave(new FocusingRecipeParams(recipe.getIngredients().get(0), new ProcessingOutput(recipe.getResultItem(registryAccess), 1F), FocusingRecipeParams.id(recipe, BeamTypeCondition.MICROWAVE)))),

        ADD_COLOR(BeamTypeConditionProfile::colorItems, registryAccess -> recipe -> {
            Ingredient dye; Ingredient complement;
            if(recipe.getIngredients().get(0).getItems()[0].getItem() instanceof DyeItem){
                dye = recipe.getIngredients().get(0);
                complement = recipe.getIngredients().get(1);
            } else {
                dye = recipe.getIngredients().get(1);
                complement = recipe.getIngredients().get(0);
            }
            return FocusingRecipe.visible(new FocusingRecipeParams(List.of(complement, dye), List.of(new ProcessingOutput(recipe.getResultItem(registryAccess), 0.9F), new ProcessingOutput(new ItemStack(Items.CHARCOAL), 0.1F)), FocusingRecipeParams.id(recipe, BeamTypeCondition.MICROWAVE)));
        }),
        SANDPAPER((RecipeType<?>) AllRecipeTypes.SANDPAPER_POLISHING.getType(), registryAccess -> recipe -> {

            return FocusingRecipe.visible(new FocusingRecipeParams(List.of(recipe.getIngredients().get(0), Ingredient.of(Items.SAND)), outputCharcoal(recipe.getResultItem(registryAccess), 0.8F), FocusingRecipeParams.id(recipe, BeamTypeCondition.VISIBLE)));
        }),

        ;
        private static boolean colorItems(Recipe<?> r){
            return r.getIngredients().size() == 2 && (r.getIngredients().get(0).getItems()[0].getItem() instanceof DyeItem ^ r.getIngredients().get(1).getItems()[0].getItem() instanceof DyeItem);
        }
        private static List<ProcessingOutput> outputCharcoal(ItemStack item, float successChance){
            return List.of(new ProcessingOutput(item, successChance), new ProcessingOutput(new ItemStack(Items.CHARCOAL), 1.0F - successChance));
        }

        private final Predicate<Recipe<?>> recipePredicate;
        private final Function<RegistryAccess, Function<Recipe<?>, FocusingRecipe>> converter;
        private static final List<FocusingRecipe> RECIPES = new ArrayList<>();

        BeamTypeConditionProfile(RecipeType<?> recipeType, Function<RegistryAccess, Function<Recipe<?>, FocusingRecipe>> converter){
            this(r -> r.getType().equals(recipeType), converter);
        }

        BeamTypeConditionProfile(Predicate<Recipe<?>> recipePredicate, Function<RegistryAccess, Function<Recipe<?>, FocusingRecipe>> converter) {
            this.recipePredicate = recipePredicate;
            this.converter = converter;

        }


        public Function<Recipe<?>, FocusingRecipe> getConverter(RegistryAccess registryAccess) {
            return converter.apply(registryAccess);
        }

        public Predicate<Recipe<?>> getRecipePredicate() {
            return recipePredicate;
        }

        public static void initialize(Level level){
            if(level != null){
                initializeRecipes(level);
            }
        }
        public static void initializeRecipes(Level level){
            if(!RECIPES.isEmpty()) return;
            Arrays.stream(BeamTypeConditionProfile.values()).forEach(b -> {
                level.getRecipeManager().getRecipes().forEach(recipe -> {
                    if (b.recipePredicate.test(recipe)) {
                        RECIPES.add(b.getConverter(level.registryAccess()).apply(recipe));
                    }
                });
            });
        }
        public Optional<FocusingRecipe> getRecipe(Level level, RecipeWrapper recipeWrapper, BeamHelper.BeamType b){
            List<FocusingRecipe> focusingRecipes = RECIPES.stream().filter(r -> r.matches(recipeWrapper, level) && r.beamTypeCondition.test(b)).toList();
            return Optional.ofNullable(focusingRecipes.isEmpty() ? null : focusingRecipes.get(0));
        }
        public static boolean canBeProcessed(Level level, RecipeWrapper recipeWrapper, BeamHelper.BeamType b){
            return getRecipeFor(level, recipeWrapper, b).isPresent();
        }

        public static Optional<FocusingRecipe> getRecipeFor(Level level, RecipeWrapper recipeWrapper, BeamHelper.BeamType beamType){
            List<FocusingRecipe> focusingRecipes = Arrays.stream(BeamTypeConditionProfile.values()).map(b -> b.getRecipe(level, recipeWrapper, beamType)).filter(Optional::isPresent).map(Optional::get).toList();
            return Optional.ofNullable(focusingRecipes.isEmpty() ? null : focusingRecipes.get(0));

        }
    }

}
