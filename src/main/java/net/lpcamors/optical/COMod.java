package net.lpcamors.optical;

import com.mojang.datafixers.types.Func;
import com.mojang.logging.LogUtils;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.ModFilePackResources;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.utility.Components;
import net.lpcamors.optical.blocks.COBlocks;
import net.lpcamors.optical.blocks.COBlockEntities;
import net.lpcamors.optical.data.COLang;
import net.lpcamors.optical.data.CODataGen;
import net.lpcamors.optical.items.COItems;
import net.lpcamors.optical.ponder.COPonderIndex;
import net.lpcamors.optical.ponder.COPonderTags;
import net.lpcamors.optical.recipes.FocusingRecipeParams;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.locating.IModFile;
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

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBus {

        @SubscribeEvent
        public static void addPackFinders(AddPackFindersEvent event) {
            if (event.getPackType() == PackType.CLIENT_RESOURCES) {
                IModFileInfo modFileInfo = ModList.get().getModFileById(COMod.ID);
                if (modFileInfo == null) {
                    COMod.LOGGER.error("Could not find Create mod file info; built-in resource packs will be missing!");
                    return;
                }
                IModFile modFile = modFileInfo.getFile();
                event.addRepositorySource(consumer -> {
                    Pack pack = Pack.readMetaAndCreate(loc("legacy_optical_copper").toString(), Components.literal("Create Optical Legacy Copper Compatibility"), false, id -> new ModFilePackResources(id, modFile, "resourcepacks/legacy_optical_copper"), PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
                    if (pack != null) {
                        consumer.accept(pack);
                    }
                });
            }
        }
    }


}
