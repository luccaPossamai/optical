package net.lpcamors.optical.network;

import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.lpcamors.optical.blocks.hologram_source.HologramSourceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;

public class ConfigureHologramSourcePacket extends BlockEntityConfigurationPacket<HologramSourceBlockEntity> {

    private CompoundTag tag;

    public ConfigureHologramSourcePacket(FriendlyByteBuf buffer) {
        super(buffer);
    }

    public ConfigureHologramSourcePacket(BlockPos blockPos, CompoundTag tag) {
        super(blockPos);
        this.tag = tag;

    }

    @Override
    protected void writeSettings(FriendlyByteBuf buffer) {
        buffer.writeNbt(this.tag);
    }

    @Override
    protected void readSettings(FriendlyByteBuf buffer) {
        this.tag = buffer.readNbt();
    }

    @Override
    protected void applySettings(HologramSourceBlockEntity be) {
        try {
            HologramSourceBlockEntity controller = be.getController();
            controller.setMode(tag.getInt("ModeIndex"));
            controller.setFixedAngle(tag.getInt("Angle"));
            be.sendData();
            controller.onConnection(controller.getBlockPos(), false, opBe -> opBe.ifPresent(be1 -> be1.updateConnection(controller)));

        } catch (Exception ex){
            System.out.println("Unable to send data to server in "+be.toString());
        }

    }
}
