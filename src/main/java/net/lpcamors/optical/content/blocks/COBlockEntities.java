package net.lpcamors.optical.content.blocks;

import com.simibubi.create.content.kinetics.base.HorizontalHalfShaftInstance;
import com.simibubi.create.content.kinetics.base.ShaftInstance;
import com.simibubi.create.content.kinetics.base.ShaftRenderer;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.lpcamors.optical.content.blocks.absorption_polarizing_filter.AbsorptionPolarizingFilterBlockEntity;
import net.lpcamors.optical.content.blocks.mirror.EncasedMirrorBlockEntity;
import net.lpcamors.optical.content.blocks.optical_sensor.OpticalSensorBlockEntity;
import net.lpcamors.optical.content.blocks.optical_receptor.OpticalReceptorBlockEntity;
import net.lpcamors.optical.content.blocks.optical_source.OpticalSourceBlockEntity;
import net.lpcamors.optical.content.blocks.polarizing_beam_splitter_block.PolarizingBeamSplitterBlockEntity;
import net.lpcamors.optical.content.rendererers.*;

import static com.simibubi.create.Create.REGISTRATE;

public class COBlockEntities {

    public static void initiate(){}

    public static final BlockEntityEntry<OpticalSourceBlockEntity> OPTICAL_SOURCE = REGISTRATE
            .blockEntity("optical_source", OpticalSourceBlockEntity::new)
            .instance(() -> HorizontalHalfShaftInstance::new)
            .validBlocks(COBlocks.OPTICAL_SOURCE)
            .renderer(() -> OpticalSourceRenderer::new)
            .register();

    public static final BlockEntityEntry<OpticalReceptorBlockEntity> OPTICAL_RECEPTOR = REGISTRATE
            .blockEntity("optical_receptor", OpticalReceptorBlockEntity::new)
            .instance(() -> ShaftInstance::new)
            .validBlocks(COBlocks.OPTICAL_RECEPTOR)
            .renderer(() -> ShaftRenderer::new)
            .register();
    public static final BlockEntityEntry<EncasedMirrorBlockEntity> ENCASED_MIRROR = REGISTRATE
            .blockEntity("encased_mirror", EncasedMirrorBlockEntity::new)
            .instance(() -> ShaftInstance::new)
            .validBlocks(COBlocks.ENCASED_MIRROR)
            .renderer(() -> EncasedMirrorRenderer::new)
            .register();

    public static final BlockEntityEntry<AbsorptionPolarizingFilterBlockEntity> ABSORPTION_POLARIZING_FILTER = REGISTRATE
            .blockEntity("absorption_polarizing_filter", AbsorptionPolarizingFilterBlockEntity::new)
            .validBlocks(COBlocks.ABSORPTION_POLARIZING_FILTER)
            .renderer(() -> AbsorptionPolarizingFilterRenderer::new)
            .register();


    public static final BlockEntityEntry<OpticalSensorBlockEntity> OPTICAL_SENSOR = REGISTRATE
            .blockEntity("optical_sensor", OpticalSensorBlockEntity::new)
            .validBlocks(COBlocks.OPTICAL_SENSOR)
            .renderer(() -> OpticalSensorRenderer::new)
            .register();
    public static final BlockEntityEntry<PolarizingBeamSplitterBlockEntity> POLARIZING_BEAM_SPLITTER = REGISTRATE
            .blockEntity("polarizing_beam_splitter", PolarizingBeamSplitterBlockEntity::new)
            .validBlocks(COBlocks.POLARIZING_BEAM_SPLITTER_BLOCK)
            .renderer(() -> PolarizingBeamSplitterRenderer::new)
            .register();


}
