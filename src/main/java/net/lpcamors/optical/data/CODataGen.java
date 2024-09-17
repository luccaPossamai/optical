package net.lpcamors.optical.data;

import com.simibubi.create.foundation.ponder.PonderLocalization;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.ponder.COPonderIndex;
import net.lpcamors.optical.ponder.COPonderTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

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
            generator.addProvider(true, new FocusingRecipeGen(output));

        }
        providePonderLang();

    }


    private static void providePonderLang() {
        COPonderTags.initiate();
        COPonderIndex.initiate();
        PonderLocalization.provideRegistrateLang(COMod.REGISTRATE);
    }


}
