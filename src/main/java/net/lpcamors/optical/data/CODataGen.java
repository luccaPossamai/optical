package net.lpcamors.optical.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.data.recipe.SequencedAssemblyRecipeGen;
import com.simibubi.create.foundation.data.recipe.StandardRecipeGen;
import com.simibubi.create.foundation.ponder.PonderLocalization;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.FilesHelper;
import com.simibubi.create.infrastructure.data.CreateDatagen;
import com.simibubi.create.infrastructure.data.CreateRegistrateTags;
import com.simibubi.create.infrastructure.data.GeneratedEntriesProvider;
import com.simibubi.create.infrastructure.ponder.AllPonderTags;
import com.simibubi.create.infrastructure.ponder.GeneralText;
import com.simibubi.create.infrastructure.ponder.PonderIndex;
import com.simibubi.create.infrastructure.ponder.SharedText;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.providers.ProviderType;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.ponder.COPonderIndex;
import net.lpcamors.optical.ponder.COPonderTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.core.config.plugins.Plugin;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class CODataGen {

    public static void dataGen(GatherDataEvent event){
        //addExtraRegistrateData();
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        if (event.includeServer()) {
            COEntriesProvider generatedEntriesProvider = new COEntriesProvider(output, lookupProvider);
            generator.addProvider(true, new COBlockTagsProvider(output, lookupProvider, existingFileHelper));
            generator.addProvider(true, generatedEntriesProvider);
            generator.addProvider(true, new COSequencedAssemblyRecipeProvider(output));
        }
        providePonderLang();

    }


    private static void providePonderLang() {
        COPonderTags.initiate();
        COPonderIndex.initiate();
        PonderLocalization.provideRegistrateLang(COMod.REGISTRATE);
    }


}
