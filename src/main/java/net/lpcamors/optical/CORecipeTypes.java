package net.lpcamors.optical;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeSerializer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.Lang;
import net.lpcamors.optical.recipes.FocusingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


public enum CORecipeTypes implements IRecipeTypeInfo {

    FOCUSING(FocusingRecipe::new)
;
    private final ResourceLocation id;
    private final RegistryObject<RecipeSerializer<?>> serializerObject;
    @Nullable
    private final RegistryObject<RecipeType<?>> typeObject;
    private final Supplier<RecipeType<?>> type;

    CORecipeTypes(Supplier<RecipeSerializer<?>> serializerSupplier) {
        String name = Lang.asId(name());
        id = COMod.loc(name);
        serializerObject = Registers.SERIALIZER_REGISTER.register(name, serializerSupplier);
        typeObject = Registers.TYPE_REGISTER.register(name, () -> RecipeType.simple(id));
        type = typeObject;
    }

    CORecipeTypes(ProcessingRecipeBuilder.ProcessingRecipeFactory<?> processingFactory) {
        this(() -> new ProcessingRecipeSerializer<>(processingFactory));
    }

    public static void register(IEventBus modEventBus) {
        ShapedRecipe.setCraftingSize(9, 9);
        Registers.SERIALIZER_REGISTER.register(modEventBus);
        Registers.TYPE_REGISTER.register(modEventBus);
    }

    public static class Registers {
        protected static final DeferredRegister<RecipeSerializer<?>> SERIALIZER_REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, COMod.ID);
        protected static final DeferredRegister<RecipeType<?>> TYPE_REGISTER = DeferredRegister.create(Registries.RECIPE_TYPE, COMod.ID);

    }
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public <T extends RecipeSerializer<?>> T getSerializer() {
        return (T) this.serializerObject.get();
    }

    @Override
    public <T extends RecipeType<?>> T getType() {
        return (T) this.type.get();
    }




//
//    public static final Supplier<RecipeType<FocusingRecipe>> FOCUSING = register("focusing");
//
//    public static RegistryObject<RecipeSerializer<?>> RADIO_SERIALIZER = SERIALIZER_REGISTER.register(
//            "radio_focusing", () -> serializer(FocusingRecipeParams.BeamTypeCondition.RADIO)
//           );
//    public static RegistryObject<RecipeSerializer<?>> MICROWAVE_SERIALIZER = SERIALIZER_REGISTER.register(
//            "microwave_focusing", () -> serializer(FocusingRecipeParams.BeamTypeCondition.MICROWAVE)
//    );
//    public static RegistryObject<RecipeSerializer<?>> VISIBLE_SERIALIZER = SERIALIZER_REGISTER.register(
//            "visible_focusing", () -> serializer(FocusingRecipeParams.BeamTypeCondition.VISIBLE)
//    );
//    public static RegistryObject<RecipeSerializer<?>> GAMMA_SERIALIZER = SERIALIZER_REGISTER.register(
//            "gamma_focusing", () -> serializer(FocusingRecipeParams.BeamTypeCondition.GAMMA)
//    );
//    public static RegistryObject<RecipeSerializer<?>> NONE_SERIALIZER = SERIALIZER_REGISTER.register(
//            "general_focusing", () -> serializer(FocusingRecipeParams.BeamTypeCondition.NONE)
//    );
//
//    private static <T extends Recipe<?>> Supplier<RecipeType<T>> register(String id) {
//        return TYPE_REGISTER.register(id, () -> new RecipeType<>() {
//            public String toString() {
//                return id;
//            }
//        });
//    }
//    private static ProcessingRecipeSerializer<FocusingRecipe> serializer(FocusingRecipeParams.BeamTypeCondition b){
//        return new SequencedAssemblyFocusingRecipeSerializer(new FocusingRecipeProcessingFactory(b));
//    }



}
