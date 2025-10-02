package com.berksire.furniture.core.block.entity;

import com.berksire.furniture.core.registry.EntityTypeRegistry;
import com.berksire.furniture.core.util.FurnitureUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FlowerPotBigBlockEntity extends BlockEntity {
    private Item flower;

    public FlowerPotBigBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.FLOWER_POT_BIG_ENTITY.get(), pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.saveAdditional(nbt, provider);
        this.writeFlower(nbt, this.flower, provider);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        this.flower = this.readFlower(nbt, provider);
    }

    private void writeFlower(CompoundTag nbt, Item flower, HolderLookup.Provider provider) {
        if (flower != null) {
            ItemStack stack = flower.getDefaultInstance();
            CompoundTag stackTag = (CompoundTag) stack.save(provider);
            nbt.put("flower", stackTag);
        } else {
            nbt.put("flower", new CompoundTag());
        }
    }

    private Item readFlower(CompoundTag nbt, HolderLookup.Provider provider) {
        if (nbt.contains("flower")) {
            CompoundTag stackTag = nbt.getCompound("flower");
            if (!stackTag.isEmpty()) {
                return ItemStack.parse(provider, stackTag)
                        .orElse(ItemStack.EMPTY)
                        .getItem();
            }
        }
        return null;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveCustomOnly(provider);
    }

    @Override
    public void setChanged() {
        if (this.level instanceof ServerLevel serverLevel) {
            Packet<ClientGamePacketListener> updatePacket = this.getUpdatePacket();
            if (updatePacket != null) {
                for (ServerPlayer player : FurnitureUtil.tracking(serverLevel, this.getBlockPos())) {
                    player.connection.send(updatePacket);
                }
            }
        }
        super.setChanged();
    }
}
