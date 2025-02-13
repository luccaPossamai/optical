package net.lpcamors.optical.blocks.optical_receptor;

import com.simibubi.create.content.kinetics.gauge.GaugeBlock;
import com.simibubi.create.foundation.data.DirectionalAxisBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.ModelFile;

public class OpticalReceptorGenerator {

    public static Receptor LIGHT = new Receptor("optical_receptor");
    public static Receptor HEAVY = new Receptor("heavy_optical_receptor");

    public static class Receptor extends DirectionalAxisBlockStateGen {

        private final String name;
        protected Receptor(String name){
            this.name = name;
        }

        @Override
        public <T extends Block> String getModelPrefix(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov, BlockState state) {
            return "block/"+this.name+"/base";
        }

        @Override
        public <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                    BlockState state) {
            Direction direction = state.getValue(GaugeBlock.FACING);
            boolean alongFirst = state.getValue(GaugeBlock.AXIS_ALONG_FIRST_COORDINATE);
            boolean f0 = direction.getAxis().isVertical();
            boolean f1 = direction.getAxis().isHorizontal() && (direction.getAxis() == Direction.Axis.X) == alongFirst;
            String partial = f1 ? "_vertical" : f0 ? "" : "_wall";
            return prov.models()
                    .getExistingFile(prov.modLoc(getModelPrefix(ctx, prov, state) + partial));
        }

    }


}
