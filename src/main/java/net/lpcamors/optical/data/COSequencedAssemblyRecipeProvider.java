package net.lpcamors.optical.data;

import com.google.common.base.Supplier;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.COUtils;
import net.lpcamors.optical.blocks.COBlocks;
import net.lpcamors.optical.items.COItems;
import net.lpcamors.optical.recipes.FocusingRecipe;
import net.lpcamors.optical.recipes.FocusingRecipeParams;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;

import java.io.FileOutputStream;
import java.util.List;
import java.util.function.UnaryOperator;

public class COSequencedAssemblyRecipeProvider extends CreateRecipeProvider {

    GeneratedRecipe
            MIRROR = createSequenced("mirror_item", b -> b.require(Items.GLASS_PANE)
                    .transitionTo(COItems.INCOMPLETE_MIRROR)
                    .addOutput(COItems.MIRROR, 100)
                    .loops(1)
                    .addStep(FillingRecipe::new, rb -> rb.require(Fluids.WATER, 250))
                    .addStep(PressingRecipe::new, rb -> rb)
                    .addStep(PressingRecipe::new, rb -> rb)),
            POLARIZING_FILTER = createSequenced("polarizing_filter", b -> b.require(Items.TINTED_GLASS)
                    .transitionTo(COItems.INCOMPLETE_POLARIZING_FILTER)
                    .addOutput(COItems.POLARIZING_FILTER, 100)
                    .loops(1)
                    .addStep(FillingRecipe::new, rb -> rb.require(Fluids.WATER, 250))
                    .addStep(PressingRecipe::new, rb -> rb)
                    .addStep(PressingRecipe::new, rb -> rb)),
            OPTICAL_DEVICE = createSequenced("optical_device", b -> b.require(Items.AMETHYST_SHARD)
                    .transitionTo(COItems.INCOMPLETE_OPTICAL_DEVICE)
                    .addOutput(COItems.OPTICAL_DEVICE, 100)
                    .loops(3)
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllItems.IRON_SHEET))
                    .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.GLASS_PANE))
                    .addStep(FillingRecipe::new, rb -> rb.require(Fluids.WATER, 500))
                    .addStep(PressingRecipe::new, rb -> rb)),
            OPTICAL_DEVICE_FOCUSING = createSequenced("optical_device_focusing", b -> b.require(AllItems.IRON_SHEET)
                            .transitionTo(COItems.INCOMPLETE_OPTICAL_DEVICE)
                            .addOutput(COItems.OPTICAL_DEVICE, 100)
                            .loops(1)
                            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.AMETHYST_SHARD))
                            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllItems.IRON_SHEET))
                            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(Items.GLASS_PANE))
                            .addStep(DeployerApplicationRecipe::new, rb -> rb.require(AllItems.IRON_SHEET))
                            .addStep(FocusingRecipe::gamma, p -> p)),
            COPPER_COIL = coil("copper", COItems.COPPER_COIL, 3),
            GOLDEN_COIL = coil("golden", COItems.GOLDEN_COIL, 6),
            ZINC_COIL = coil("zinc", COItems.ZINC_COIL, 4),
            ROSE_QUARTZ_CATALYST_COIL = coil("rose_quartz_catalyst", COItems.ROSE_QUARTZ_CATALYST_COIL, 3,
                    FocusingRecipe::gamma, rb -> rb),
            OPTICAL_SOURCE = viaShaped(COBlocks.OPTICAL_SOURCE::asItem,
                    b -> b.define('C', AllBlocks.COGWHEEL)
                            .define('A', AllBlocks.ANDESITE_CASING)
                            .define('S', AllBlocks.SHAFT)
                            .define('P', COItems.POLARIZING_FILTER)
                            .define('L', COItems.OPTICAL_DEVICE)
                            .pattern(" C ")
                            .pattern("LAP")
                            .pattern(" S "),
                    has(AllBlocks.ANDESITE_CASING::get)),
            THERMAL_OPTICAL_SOURCE = viaShaped(COBlocks.THERMAL_OPTICAL_SOURCE::asItem,
                            b -> b.define('C', Items.COPPER_INGOT)
                                    .define('T', AllBlocks.FLUID_TANK)
                                    .define('S', AllBlocks.SHAFT)
                                    .define('P', COItems.POLARIZING_FILTER)
                                    .define('L', COItems.OPTICAL_DEVICE)
                                    .pattern("CCC")
                                    .pattern("LTP")
                                    .pattern("CSC"),
                            has(AllBlocks.ANDESITE_CASING::get)),
            HOLOGRAM_SOURCE = viaShaped(COBlocks.HOLOGRAM_SOURCE::asItem,
                            b -> b.define('Z', COItems.ZINC_COIL)
                                    .define('C', AllBlocks.ANDESITE_CASING)
                                    .define('L', COItems.OPTICAL_DEVICE)
                                    .define('I', AllItems.ZINC_INGOT)
                                    .pattern("IZI")
                                    .pattern("LCL"),
                            has(AllBlocks.ANDESITE_CASING::get)),
            LIGHT_RECEPTOR = viaShaped(COBlocks.LIGHT_OPTICAL_RECEPTOR::asItem,
                    b -> b.define('A', AllBlocks.ANDESITE_CASING)
                            .define('S', AllBlocks.SHAFT)
                            .define('C', COItems.COPPER_COIL)
                            .define('L', COItems.OPTICAL_DEVICE)
                            .pattern(" L ")
                            .pattern("SCS")
                            .pattern(" A "),
                    has(AllBlocks.ANDESITE_CASING::get)),
            HEAVY_RECEPTOR = viaShaped(COBlocks.HEAVY_OPTICAL_RECEPTOR::asItem,
                            b -> b.define('A', AllBlocks.ANDESITE_CASING)
                                    .define('S', AllBlocks.SHAFT)
                                    .define('C', COItems.GOLDEN_COIL)
                                    .define('L', COItems.OPTICAL_DEVICE)
                                    .pattern(" L ")
                                    .pattern("SCS")
                                    .pattern(" A "),
                            has(AllBlocks.ANDESITE_CASING::get)),
            CONDENSER = viaShaped(COBlocks.BEAM_CONDENSER::asItem,
                            b -> b.define('A', AllBlocks.ANDESITE_CASING)
                                    .define('C', COItems.ROSE_QUARTZ_CATALYST_COIL)
                                    .define('L', COItems.OPTICAL_DEVICE)
                                    .pattern(" L ")
                                    .pattern("LCL")
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
                            .define('S', AllBlocks.SHAFT)
                            .pattern("A")
                            .pattern("M")
                            .pattern("S"),
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
                    has(AllBlocks.ANDESITE_CASING::get)),
            FOCUSER = viaShaped(COBlocks.BEAM_FOCUSER::asItem, b ->
                    b.define('A', AllBlocks.ANDESITE_CASING)
                            .define('B', COItems.OPTICAL_DEVICE)
                            .define('C', AllItems.IRON_SHEET)
                            .define('D', AllBlocks.SHAFT)
                            .pattern(" B ")
                            .pattern("DAD")
                            .pattern(" C "),
                    has(AllBlocks.ANDESITE_CASING::get))
    ;


    public <T extends ProcessingRecipe<?>> GeneratedRecipe coil(String name, ItemEntry<?> item, int loops) {
        return createSequenced(name+"_coil", b -> b.require(AllItems.ANDESITE_ALLOY)
                .transitionTo(COUtils.EQ_INCOMPLETE.get(item))
                .addOutput(item, 100)
                .addOutput(COUtils.EQ_SHEETS.get(item), 10)
                .loops(loops)
                .addStep(DeployerApplicationRecipe::new, rb -> rb.require(COUtils.EQ_SHEETS.get(item)))
                .addStep(FillingRecipe::new, rb -> rb.require(Fluids.LAVA, 250))
                .addStep(PressingRecipe::new, rb -> rb));
    }


        public <T extends ProcessingRecipe<?>> GeneratedRecipe coil(String name, ItemEntry<?> item, int loops, ProcessingRecipeBuilder.ProcessingRecipeFactory<T> factory, UnaryOperator<ProcessingRecipeBuilder<T>> builder){
        return createSequenced(name+"_coil", b -> b.require(AllItems.ANDESITE_ALLOY)
                .transitionTo(COUtils.EQ_INCOMPLETE.get(item))
                .addOutput(item, 100)
                .addOutput(COUtils.EQ_SHEETS.get(item), 10)
                .loops(loops)
                .addStep(DeployerApplicationRecipe::new, rb -> rb.require(COUtils.EQ_SHEETS.get(item)))
                .addStep(FillingRecipe::new, rb -> rb.require(Fluids.LAVA, 250))
                .addStep(PressingRecipe::new, rb -> rb)
                .addStep(factory, builder));
    }

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
