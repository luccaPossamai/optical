package net.lpcamors.optical;

import com.mojang.datafixers.types.Func;
import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.lpcamors.optical.blocks.COBlocks;
import net.lpcamors.optical.blocks.COBlockEntities;
import net.lpcamors.optical.data.COLang;
import net.lpcamors.optical.data.CODataGen;
import net.lpcamors.optical.items.COItems;
import net.lpcamors.optical.ponder.COPonderIndex;
import net.lpcamors.optical.ponder.COPonderTags;
import net.lpcamors.optical.recipes.FocusingRecipeParams;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.function.Function;

@Mod(COMod.ID)
public class COMod {
    public static final String ID = "create_optical";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Function<String, ResourceLocation> LOC_FUNC = s -> new ResourceLocation(ID, s);

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(COMod.ID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    public static ResourceLocation loc(String name){
        return LOC_FUNC.apply(name);
    }

    public COMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);

        REGISTRATE.registerEventListeners(modEventBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> COPartialModels::new);

        COCreativeModeTabs.initiate(modEventBus);

        COBlocks.initiate();
        COItems.initiate();
        COBlockEntities.initiate();
        CORecipeTypes.register(modEventBus);
        COLang.initiate();

        modEventBus.addListener(EventPriority.LOWEST, CODataGen::dataGen);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(this::clientSetup));
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {}

    private void clientSetup(final FMLClientSetupEvent event) {
        COPonderIndex.initiate();
        COPonderTags.initiate();
    }


}
