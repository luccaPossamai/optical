package net.lpcamors.optical.content.items;

import com.simibubi.create.AllCreativeModeTabs;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyItem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.lpcamors.optical.COCreativeModeTabs;
import net.lpcamors.optical.COMod;
import net.minecraft.world.item.Item;

import static com.simibubi.create.Create.REGISTRATE;


public class COItems {


    public static final ItemEntry<SequencedAssemblyItem>
            INCOMPLETE_GOLDEN_COIL = sequencedIngredient("incomplete_golden_coil"),
            INCOMPLETE_MIRROR = sequencedIngredient("incomplete_mirror"),
            INCOMPLETE_POLARIZING_FILTER = sequencedIngredient("incomplete_polarizing_filter"),
            INCOMPLETE_LASER = sequencedIngredient("incomplete_laser");

    public static final ItemEntry<Item>
            POLARIZING_FILTER = ingredient("polarizing_filter"),
            MIRROR = ingredient("mirror"),
            GOLDEN_COIL = ingredient("golden_coil"),
            LASER = ingredient("laser");
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
