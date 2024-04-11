package net.lpcamors.optical.data;

import com.google.common.base.Supplier;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.content.blocks.COBlocks;
import net.lpcamors.optical.content.items.COItems;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;

import java.util.function.UnaryOperator;

public class COSequencedAssemblyRecipeProvider extends CreateRecipeProvider {

    GeneratedRecipe
            GOLDEN_COIL = createSequenced("golden_coil", b -> b.require(AllItems.ANDESITE_ALLOY)
            .transitionTo(COItems.INCOMPLETE_GOLDEN_COIL)
            .addOutput(COItems.GOLDEN_COIL, 100)
            .addOutput(AllItems.GOLDEN_SHEET, 10)
            .loops(10)
            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllItems.GOLDEN_SHEET))
            .addStep(PressingRecipe::new, rb -> rb)),
            MIRROR = createSequenced("mirror_item", b -> b.require(Items.GLASS_PANE)
                    .transitionTo(COItems.INCOMPLETE_MIRROR)
                    .addOutput(COItems.MIRROR, 100)
                    .addStep(PressingRecipe::new, rb -> rb)
                    .addStep(PressingRecipe::new, rb -> rb)
                    .addStep(PressingRecipe::new, rb -> rb)),
            POLARIZING_FILTER = createSequenced("polarizing_filter", b -> b.require(Items.TINTED_GLASS)
                    .transitionTo(COItems.INCOMPLETE_POLARIZING_FILTER)
                    .addOutput(COItems.POLARIZING_FILTER, 100)
                    .addStep(PressingRecipe::new, rb -> rb)
                    .addStep(PressingRecipe::new, rb -> rb)
                    .addStep(PressingRecipe::new, rb -> rb)),
            /*POLARIZING_FILTER = create("polarizing_filter", b -> b.require(Items.TINTED_GLASS)
                    .transitionTo(COItems.INCOMPLETE_GOLDEN_COIL)
                    .addOutput(COItems.POLARIZING_FILTER,1)
                    .addStep(PressingRecipe::new, rb -> rb));

             */
            OPTICAL_SOURCE = viaShaped(COBlocks.OPTICAL_SOURCE::asItem,
                    b -> b.define('C', AllBlocks.COGWHEEL)
                            .define('A', AllBlocks.ANDESITE_CASING)
                            .define('S', AllBlocks.SHAFT)
                            .define('P', COItems.POLARIZING_FILTER)
                            .define('L', COItems.LASER)
                            .pattern(" C ")
                            .pattern("LAP")
                            .pattern(" S "),
                    has(AllBlocks.ANDESITE_CASING::get)),
            OPTICAL_RECEPTOR = viaShaped(COBlocks.OPTICAL_RECEPTOR::asItem,
                    b -> b.define('A', AllBlocks.ANDESITE_CASING)
                            .define('S', AllBlocks.SHAFT)
                            .define('G', COItems.GOLDEN_COIL)
                            .define('L', COItems.LASER)
                            .pattern(" L ")
                            .pattern("SGS")
                            .pattern(" A "),
                    has(AllBlocks.ANDESITE_CASING::get)),

            ABSORPTION_POLARIZING_FILTER = viaShaped(COBlocks.ABSORPTION_POLARIZING_FILTER::asItem,
                    b -> b.define('A', AllBlocks.ANDESITE_CASING)
                            .define('P', COItems.POLARIZING_FILTER)
                            .pattern("A")
                            .pattern("P")
                            .pattern("A"),
                    has(AllBlocks.ANDESITE_CASING::get)),
            ENCASED_MIRROR = viaShaped(COBlocks.ENCASED_MIRROR::asItem,
                    b -> b.define('A', AllBlocks.ANDESITE_CASING)
                            .define('M', COItems.MIRROR)
                            .pattern("A")
                            .pattern("M")
                            .pattern("A"),
                    has(AllBlocks.ANDESITE_CASING::get)),
            POLARIZING_BEAM_SPLITTER = viaShaped(COBlocks.POLARIZING_BEAM_SPLITTER_BLOCK::asItem,
                    b -> b.define('A', AllBlocks.ANDESITE_CASING)
                            .define('M', COItems.MIRROR)
                            .pattern(" M ")
                            .pattern("MAM")
                            .pattern(" M "),
                    has(AllBlocks.ANDESITE_CASING::get)),
            OPTICAL_SENSOR = viaShaped(COBlocks.OPTICAL_SENSOR::asItem,
                    b -> b.define('S', Tags.Items.STONE)
                            .define('B', AllTags.forgeItemTag("plates/brass"))
                            .define('E', AllItems.ELECTRON_TUBE)
                            .pattern("BEB")
                            .pattern("SSS"),
                    has(AllBlocks.ANDESITE_CASING::get));




    public COSequencedAssemblyRecipeProvider(PackOutput p_i48262_1_) {
        super(p_i48262_1_);
    }

    protected GeneratedRecipe createSequenced(String name, UnaryOperator<SequencedAssemblyRecipeBuilder> transform) {
        GeneratedRecipe generatedRecipe =
                c -> transform.apply(new SequencedAssemblyRecipeBuilder(new ResourceLocation(COMod.ID, name)))
                        .build(c);
        all.add(generatedRecipe);
        return generatedRecipe;
    }

    protected Supplier<ItemPredicate> has(Supplier<? extends ItemLike> itemLikeSupplier){
        return () -> ItemPredicate.Builder.item()
                .of(itemLikeSupplier.get())
                .build();
    }

    GeneratedRecipe viaShaped(Supplier<ItemLike> result, UnaryOperator<ShapedRecipeBuilder> builder, Supplier<ItemPredicate>  unlockedBy) {
        return viaShaped(result, 1, builder, unlockedBy);
    }

    GeneratedRecipe viaShaped(Supplier<ItemLike> result, int amount, UnaryOperator<ShapedRecipeBuilder> builder, Supplier<ItemPredicate>  unlockedBy) {
        return register(consumer -> {
            ShapedRecipeBuilder b = builder.apply(ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result.get(), amount));
            b.unlockedBy("has_item", inventoryTrigger(unlockedBy.get()));
            b.save(consumer);
        });
    }



}
