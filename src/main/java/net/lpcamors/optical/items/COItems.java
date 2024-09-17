package net.lpcamors.optical.items;

import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.lpcamors.optical.COMod;
import net.minecraft.world.item.Item;


public class COItems {


    public static final ItemEntry<SequencedAssemblyItem>
            INCOMPLETE_COPPER_COIL = sequencedIngredient("incomplete_copper_coil"),
            INCOMPLETE_GOLDEN_COIL = sequencedIngredient("incomplete_golden_coil"),
            INCOMPLETE_QUARTZ_CATALYST_COIL = sequencedIngredient("incomplete_rose_quartz_catalyst_coil"),
            INCOMPLETE_MIRROR = sequencedIngredient("incomplete_mirror"),
            INCOMPLETE_POLARIZING_FILTER = sequencedIngredient("incomplete_polarizing_filter"),
            INCOMPLETE_OPTICAL_DEVICE = sequencedIngredient("incomplete_optical_device");

    public static final ItemEntry<Item>
            POLARIZING_FILTER = ingredient("polarizing_filter"),
            MIRROR = ingredient("mirror"),
            COPPER_COIL = ingredient("copper_coil"),
            GOLDEN_COIL = ingredient("golden_coil"),
            ROSE_QUARTZ_CATALYST_COIL = ingredient("rose_quartz_catalyst_coil"),
            OPTICAL_DEVICE = ingredient("optical_device");

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
