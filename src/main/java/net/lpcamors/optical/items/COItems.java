package net.lpcamors.optical.items;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.lpcamors.optical.COMod;
import net.minecraft.world.item.Item;


public class COItems {


    public static final ItemEntry<SequencedAssemblyItem>
            INCOMPLETE_OPTICAL_DEVICE = sequencedIngredient("incomplete_optical_device"),
            INCOMPLETE_COPPER_COIL = sequencedIngredient("incomplete_copper_coil"),
            INCOMPLETE_GOLDEN_COIL = sequencedIngredient("incomplete_golden_coil"),
            INCOMPLETE_ZINC_COIL = sequencedIngredient("incomplete_zinc_coil"),
            INCOMPLETE_QUARTZ_CATALYST_COIL = sequencedIngredient("incomplete_rose_quartz_catalyst_coil"),
            INCOMPLETE_MIRROR = sequencedIngredient("incomplete_mirror"),
            INCOMPLETE_POLARIZING_FILTER = sequencedIngredient("incomplete_polarizing_filter");

    public static final ItemEntry<Item>
            OPTICAL_DEVICE = ingredient("optical_device"),
            COPPER_COIL = ingredient("copper_coil"),
            GOLDEN_COIL = ingredient("golden_coil"),
            ZINC_COIL = ingredient("zinc_coil"),
            ROSE_QUARTZ_CATALYST_COIL = ingredient("rose_quartz_catalyst_coil"),
            MIRROR = ingredient("mirror"),
            POLARIZING_FILTER = ingredient("polarizing_filter");

    private static ItemEntry<Item> ingredient(String name) {
        return COMod.REGISTRATE.item(name, Item::new)
                .register();
    }
    private static ItemEntry<SequencedAssemblyItem> sequencedIngredient(String name) {
        return COMod.REGISTRATE.item(name, SequencedAssemblyItem::new)
                .register();
    }

    public static void initiate(){}

}
