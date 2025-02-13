package net.lpcamors.optical;

import com.simibubi.create.foundation.utility.Components;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.lpcamors.optical.blocks.COBlocks;
import net.lpcamors.optical.data.COLang;
import net.lpcamors.optical.items.COItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class COCreativeModeTabs {

    private static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, COMod.ID);

    public static void initiate(IEventBus bus){
        CREATIVE_MODE_TABS.register(bus);
    }


    public static final RegistryObject<CreativeModeTab> CO_BASE_CREATIVE_TAB = CREATIVE_MODE_TABS.register("co_base",
            () -> CreativeModeTab.builder()
                    .title(COLang.Prefixes.CREATIVE_TAB.translate("co_base"))
                    .icon(COBlocks.OPTICAL_SOURCE::asStack)
                    .displayItems(DisplayItems::displayItemsGenerator)
                    .build());

    private static class DisplayItems {
        public static final Predicate<Item> EXCLUSION_PREDICATE;

        static {
            Set<Item> exclusions = new ReferenceOpenHashSet<>();
            List<ItemProviderEntry<?>> excludedItems = List.of(
                    COItems.INCOMPLETE_COPPER_COIL,
                    COItems.INCOMPLETE_GOLDEN_COIL,
                    COItems.INCOMPLETE_ZINC_COIL,
                    COItems.INCOMPLETE_QUARTZ_CATALYST_COIL,
                    COItems.INCOMPLETE_MIRROR,
                    COItems.INCOMPLETE_POLARIZING_FILTER,
                    COItems.INCOMPLETE_OPTICAL_DEVICE
            );
            for (ItemProviderEntry<?> entry : excludedItems) {
                exclusions.add(entry.asItem());
            }
            EXCLUSION_PREDICATE = exclusions::contains;
        }

        public static void displayItemsGenerator(CreativeModeTab.ItemDisplayParameters itemDisplayParameters, CreativeModeTab.Output output) {
            COMod.REGISTRATE.getAll(Registries.BLOCK).forEach(blockRegistryEntry -> {
                Item item = blockRegistryEntry.get().asItem();
                if(item != Items.AIR && !EXCLUSION_PREDICATE.test(item)){
                    output.accept(item);
                }
            });
            COMod.REGISTRATE.getAll(Registries.ITEM).forEach(itemRegistryEntry -> {
                Item item = itemRegistryEntry.get();
                if (!(item instanceof BlockItem) && !EXCLUSION_PREDICATE.test(item)) {
                    output.accept(item);
                }
            });
        }
    }


}
