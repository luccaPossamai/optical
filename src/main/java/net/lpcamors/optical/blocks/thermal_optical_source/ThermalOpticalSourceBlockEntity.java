package net.lpcamors.optical.blocks.thermal_optical_source;

import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.lpcamors.optical.blocks.optical_source.OpticalSourceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

public class ThermalOpticalSourceBlockEntity extends OpticalSourceBlockEntity {
    public SmartFluidTankBehaviour internalTank;

    public ThermalOpticalSourceBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if(this.isActive() && this.getTickCount() % 24 == 0){
            this.internalTank.getPrimaryHandler().drain(1, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        behaviours.add(internalTank = SmartFluidTankBehaviour.single(this, 1000)
                .allowExtraction()
                .allowInsertion());
    }


    @Override
    public boolean isActive() {
        return super.isActive() && this.internalTank.getPrimaryHandler().getFluidAmount() > 0;
    }
    @Override
    public float getIntensity(){
        Fluid fluid = this.internalTank.getPrimaryHandler().getFluid().getFluid();
        return super.getIntensity() * (Fluids.WATER.getSource().isSame(fluid) ? 2F : Fluids.LAVA.getSource().isSame(fluid) ? 4F : 1F);
    }


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && ThermalOpticalSourceBlock.hasPipeTowards(level, worldPosition, getBlockState(), side))
            return internalTank.getCapability()
                    .cast();
        return super.getCapability(cap, side);
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        containedFluidTooltip(tooltip, isPlayerSneaking,
                this.getCapability(ForgeCapabilities.FLUID_HANDLER));
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }





}
