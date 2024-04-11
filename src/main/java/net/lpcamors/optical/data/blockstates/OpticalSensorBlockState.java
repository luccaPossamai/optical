package net.lpcamors.optical.data.blockstates;

import com.simibubi.create.content.redstone.diodes.AbstractDiodeBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.content.blocks.optical_sensor.OpticalSensorBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.Vector;

public class OpticalSensorBlockState extends SpecialBlockStateGen {

    private Vector<ModelFile> models;

    @Override
    public final <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                      BlockState state) {
        if (models == null)
            models = createModels(ctx, prov.models());
        return models.get(getModelIndex(state));
    }

    public int getModelIndex(BlockState state){
        int i = 0;
        if(state.getValue(OpticalSensorBlock.INTENSITY) > 0){
            i += 1;
        }
        return i;
    }


    protected <T extends Block> Vector<ModelFile> createModels(DataGenContext<Block, T> ctx, BlockModelProvider prov) {
        Vector<ModelFile> models = new Vector<>(2);
        String name = ctx.getName();
        ResourceLocation block = existing("block");

        models.add(prov.withExistingParent(name, block).texture("top", texture(ctx, "sensor_off")));
        models.add(prov.withExistingParent(name+"_on", block).texture("top", texture(ctx, "sensor_on")));

        return models;
    }

    @Override
    protected int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected final int getYRotation(BlockState state) {
        return horizontalAngle(state.getValue(AbstractDiodeBlock.FACING));
    }
    protected ResourceLocation existing(String name) {
        return new ResourceLocation(COMod.ID, "block/optical_sensor/" + name);
    }
    protected <T extends Block> ResourceLocation texture(DataGenContext<Block, T> ctx, String name) {
        return new ResourceLocation(COMod.ID, "block/optical_sensor/" + name);
    }
}
