package net.lpcamors.optical.data;

import net.lpcamors.optical.COMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class COBlockTagsProvider extends BlockTagsProvider {

    public COBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, COMod.ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {
        tag(COTags.Blocks.BEAM_CAN_PASS_THROUGH).add(Blocks.AIR, Blocks.LIGHT, Blocks.TRIPWIRE, Blocks.REDSTONE_WIRE, Blocks.WATER, Blocks.REPEATER, Blocks.COMPARATOR);
        tag(COTags.Blocks.BEAM_CAN_PASS_THROUGH).addTags(
                BlockTags.ALL_HANGING_SIGNS, BlockTags.ALL_SIGNS, BlockTags.BANNERS, BlockTags.SLABS, BlockTags.WOOL_CARPETS,
                BlockTags.BUTTONS, BlockTags.PRESSURE_PLATES, BlockTags.WOODEN_TRAPDOORS, BlockTags.CANDLES, BlockTags.CLIMBABLE,
                BlockTags.FIRE, BlockTags.LEAVES, BlockTags.FLOWERS, BlockTags.CORAL_PLANTS, BlockTags.SAPLINGS, BlockTags.CROPS,
                BlockTags.RAILS);
    }
}
