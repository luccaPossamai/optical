package net.lpcamors.optical.compat.jei;

import com.simibubi.create.Create;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.ItemIcon;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.PressingCategory;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CRecipes;
import com.sun.jna.platform.win32.WinCrypt;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.CORecipeTypes;
import net.lpcamors.optical.blocks.COBlocks;
import net.lpcamors.optical.data.CODataGen;
import net.lpcamors.optical.data.FocusingRecipeGen;
import net.lpcamors.optical.recipes.FocusingRecipe;
import net.lpcamors.optical.recipes.FocusingRecipeCategory;
import net.lpcamors.optical.recipes.FocusingRecipeParams;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@JeiPlugin
public class CreateOpticalJei implements IModPlugin {

    private static ResourceLocation ID = new ResourceLocation(COMod.ID, "jei_plugin");

    private List<CreateRecipeCategory<?>> categories = new ArrayList<>();
    public IIngredientManager ingredientManager;

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        categories.clear();

        categories.add(builder(FocusingRecipe.class)
                .initializeCustomFocusingProcess()
                        .addTypedRecipes(CORecipeTypes.FOCUSING::getType)
                .catalystStack(() -> new ItemStack(COBlocks.BEAM_FOCUSER.get().asItem(), 1))
                .itemIcon(COBlocks.BEAM_FOCUSER)
                .emptyBackground(177, 103)
                .build("focusing", FocusingRecipeCategory::new));



        categories.forEach(registration::addRecipeCategories);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        this.ingredientManager = registration.getIngredientManager();
        categories.forEach(createRecipeCategory -> createRecipeCategory.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        this.categories.forEach(createRecipeCategory -> {
            createRecipeCategory.registerCatalysts(registration);
        });
    }


    private <T extends Recipe<?>> CategoryBuilder<T> builder(Class<? extends T> recipeClass) {
        return new CategoryBuilder<>(recipeClass);
    }


    private boolean coloringRecipes(Recipe<?> r){
        AtomicBoolean b = new AtomicBoolean(false);
        if(r.getIngredients().size() == 2){
            return Arrays.stream(r.getIngredients().get(0).getItems()).allMatch(i -> i.getItem() instanceof DyeItem)
                    || Arrays.stream(r.getIngredients().get(1).getItems()).allMatch(i -> i.getItem() instanceof DyeItem);
        }
        return b.get();
    }

    private static class CategoryBuilder<T extends Recipe<?>> {
        private final Class<? extends T> recipeClass;
        private final Predicate<CRecipes> predicate = cRecipes -> true;

        private IDrawable background;
        private IDrawable icon;

        private final List<Consumer<List<T>>> recipeListConsumers = new ArrayList<>();
        private final List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();

        public CategoryBuilder(Class<? extends T> recipeClass) {
            this.recipeClass = recipeClass;
        }

        public CategoryBuilder<T> addRecipeListConsumer(Consumer<List<T>> consumer) {
            recipeListConsumers.add(consumer);
            return this;
        }

        public CategoryBuilder<T> addTypedRecipesVar(Supplier<RecipeType<? extends Recipe<?>>> recipeType, FocusingRecipeParams.BeamTypeCondition beamTypeCondition) {
            return addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(t -> {
                Ingredient ingredient = t.getIngredients().get(0);
                ProcessingOutput p = new ProcessingOutput(t.getResultItem(Minecraft.getInstance().level.registryAccess()), 1F);
                recipes.add((T) createFocusingRecipeOf(ingredient, p, beamTypeCondition, id(t, beamTypeCondition)));
                }, recipeType.get()));
        }

        static ResourceLocation id(Recipe<?> r, FocusingRecipeParams.BeamTypeCondition beamTypeCondition){
            return new ResourceLocation(COMod.ID, r.getId().getPath() + "_focusing_" +beamTypeCondition.name().toLowerCase());
        }

        public CategoryBuilder<T> initializeCustomFocusingProcess(){
            Arrays.stream(FocusingRecipeParams.BeamTypeConditionProfile.values()).forEach(beamTypeConditionProfile -> {
                this.addAllRecipesIf(beamTypeConditionProfile.getRecipePredicate(), (Function<Recipe<?>, T>) beamTypeConditionProfile.getConverter(Minecraft.getInstance().level.registryAccess()));
            });
            return this;
        }

        public FocusingRecipe createFocusingRecipeOf(Ingredient ingredient, ProcessingOutput processingOutput, FocusingRecipeParams.BeamTypeCondition beamTypeCondition, ResourceLocation id){
            FocusingRecipe focusingRecipe = new FocusingRecipe(new FocusingRecipeParams(ingredient, processingOutput, id));
            focusingRecipe.beamTypeCondition = beamTypeCondition;
            return focusingRecipe;
        }

        public CategoryBuilder<T> addTypedRecipes(Supplier<RecipeType<? extends T>> recipeType) {
            return addRecipeListConsumer(recipes -> CreateJEI.<T>consumeTypedRecipes(recipes::add, recipeType.get()));
        }


        public CategoryBuilder<T> addAllRecipesIf(Predicate<Recipe<?>> pred, Function<Recipe<?>, T> converter) {
            return addRecipeListConsumer(recipes -> CreateJEI.consumeAllRecipes(recipe -> {
                if (pred.test(recipe)) {
                    recipes.add(converter.apply(recipe));
                }
            }));
        }


        public CategoryBuilder<T> catalystStack(Supplier<ItemStack> supplier) {
            catalysts.add(supplier);
            return this;
        }

        public CategoryBuilder<T> catalyst(Supplier<ItemLike> supplier) {
            return catalystStack(() -> new ItemStack(supplier.get()
                    .asItem()));
        }

        public CategoryBuilder<T> doubleItemIcon(ItemLike item1, ItemLike item2) {
            icon(new DoubleItemIcon(() -> new ItemStack(item1), () -> new ItemStack(item2)));
            return this;
        }


        public CategoryBuilder<T> icon(IDrawable icon) {
            this.icon = icon;
            return this;
        }

        public CategoryBuilder<T> itemIcon(ItemLike item) {
            icon(new ItemIcon(() -> new ItemStack(item)));
            return this;
        }

        public CategoryBuilder<T> background(IDrawable background) {
            this.background = background;
            return this;
        }

        public CategoryBuilder<T> emptyBackground(int width, int height) {
            background(new EmptyBackground(width, height));
            return this;
        }


        public CreateRecipeCategory<T> build(String name, CreateRecipeCategory.Factory<T> factory) {
            Supplier<List<T>> recipesSupplier;
            if (predicate.test(AllConfigs.server().recipes)) {
                recipesSupplier = () -> {
                    List<T> recipes = new ArrayList<>();
                    for (Consumer<List<T>> consumer : recipeListConsumers)
                        consumer.accept(recipes);
                    return recipes;
                };
            } else {
                recipesSupplier = () -> Collections.emptyList();
            }

            CreateRecipeCategory.Info<T> info = new CreateRecipeCategory.Info<>(
                    new mezz.jei.api.recipe.RecipeType<>(COMod.loc(name), recipeClass),
                    Component.translatable(COMod.ID + ".recipe." + name), background, icon, recipesSupplier, catalysts);
            CreateRecipeCategory<T> category = factory.create(info);
            return category;
        }
    }




}
