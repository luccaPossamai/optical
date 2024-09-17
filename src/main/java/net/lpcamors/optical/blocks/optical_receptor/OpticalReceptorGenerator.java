package net.lpcamors.optical.blocks.optical_receptor;

import com.simibubi.create.foundation.data.DirectionalAxisBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class OpticalReceptorGenerator {

    public static class LightReceptor extends DirectionalAxisBlockStateGen{
        @Override
        public <T extends Block> String getModelPrefix(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
            return "block/optical_receptor/base";
        }
    }

    public static class HeavyReceptor extends DirectionalAxisBlockStateGen{
        @Override
        public <T extends Block> String getModelPrefix(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
            return "block/heavy_optical_receptor/base";
        }
    }


}
