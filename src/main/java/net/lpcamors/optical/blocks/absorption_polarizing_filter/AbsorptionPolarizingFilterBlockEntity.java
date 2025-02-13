package net.lpcamors.optical.blocks.absorption_polarizing_filter;

import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Components;
import com.simibubi.create.foundation.utility.Lang;
import net.lpcamors.optical.COMod;
import net.lpcamors.optical.blocks.optical_source.BeamHelper;
import net.lpcamors.optical.data.COLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class AbsorptionPolarizingFilterBlockEntity extends SmartBlockEntity implements IHaveGoggleInformation {

    public AbsorptionPolarizingFilterBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        Minecraft mc = Minecraft.getInstance();
        Direction direction = ((BlockHitResult) mc.hitResult).getDirection();
        Direction blockDirection = this.getBlockState().getValue(AbsorptionPolarizingFilter.FACING).getClockWise();
        if (direction.getAxis().equals(blockDirection.getAxis())) {
            Lang.builder("tooltip").translate(COMod.ID + ".gui.goggles.absorption_polarizing_filter").forGoggles(tooltip);
            BeamHelper.BeamPolarization beamPolarization = this.getBlockState().getValue(AbsorptionPolarizingFilter.POLARIZATION);

            if (beamPolarization.isDiagonal() && !direction.equals(blockDirection)) {
                beamPolarization = beamPolarization.getNextRotated(2);
            }
            Lang.text("").add(COLang.Prefixes.CREATE.translate(("gui.goggles.polarization")).withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
            Lang.text("").add(COLang.Prefixes.CREATE.translate(beamPolarization.getDescriptionId()).append(" " + beamPolarization.getsIcon()).withStyle(ChatFormatting.AQUA)).forGoggles(tooltip, 1);

        }
        return true;
    }

}
