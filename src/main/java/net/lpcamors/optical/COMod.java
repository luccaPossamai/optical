package net.lpcamors.optical;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.lpcamors.optical.content.blocks.COBlocks;
import net.lpcamors.optical.content.blocks.COBlockEntities;
import net.lpcamors.optical.data.COLang;
import net.lpcamors.optical.data.CODataGen;
import net.lpcamors.optical.content.items.COItems;
import net.lpcamors.optical.ponder.COPonderIndex;
import net.lpcamors.optical.ponder.COPonderTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(COMod.ID)
public class COMod {
    public static final String ID = "create_optical";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(COMod.ID);

    static {
        REGISTRATE.setTooltipModifierFactory(item -> {
            return new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
                    .andThen(TooltipModifier.mapNull(KineticStats.create(item)));
        });
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
