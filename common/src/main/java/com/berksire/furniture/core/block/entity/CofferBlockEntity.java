package com.berksire.furniture.core.block.entity;

import com.berksire.furniture.core.block.CofferBlock;
import com.berksire.furniture.core.registry.EntityTypeRegistry;
import com.berksire.furniture.core.registry.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CofferBlockEntity extends RandomizableContainerBlockEntity implements LidBlockEntity {
    private NonNullList<ItemStack> items;
    private final ContainerOpenersCounter openersCounter;
    private final ChestLidController chestLidController = new ChestLidController();

    public CofferBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.COFFER_BLOCK_ENTITY.get(), pos, state);
        this.items = NonNullList.withSize(36, ItemStack.EMPTY);
        this.openersCounter = new ContainerOpenersCounter() {
            protected void onOpen(Level level, BlockPos pos, BlockState state) {
                CofferBlockEntity.this.playOpenSound(state);
            }

            protected void onClose(Level level, BlockPos pos, BlockState state) {
                CofferBlockEntity.this.playCloseSound(state);
            }

            protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int before, int after) {
                CofferBlockEntity.this.signalOpenCount(level, pos, state, before, after);
            }

            protected boolean isOwnContainer(Player player) {
                if (player.containerMenu instanceof ChestMenu chestMenu) {
                    return chestMenu.getContainer() == CofferBlockEntity.this;
                }
                return false;
            }
        };
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, provider);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, provider);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (this.getLootTable() == null && this.level != null) {
            CompoundTag tag = new CompoundTag();
            HolderLookup.Provider provider = this.level.registryAccess();
            ContainerHelper.saveAllItems(tag, this.items, provider);
            if (tag.contains("Items", 9) && !tag.getList("Items", 10).isEmpty()) {
                builder.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(tag));
            }
        }
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        CustomData data = input.get(DataComponents.BLOCK_ENTITY_DATA);
        if (data != null && this.level != null) {
            CompoundTag tag = data.copyTag();
            if (tag.contains("Items", 9) && !tag.getList("Items", 10).isEmpty() && !this.tryLoadLootTable(tag)) {
                this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
                HolderLookup.Provider provider = this.level.registryAccess();
                ContainerHelper.loadAllItems(tag, this.items, provider);
            }
        }
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int syncId, Inventory inv) {
        return new ChestMenu(MenuType.GENERIC_9x4, syncId, inv, this, 4);
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public int getContainerSize() {
        return 36;
    }

    public void startOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.incrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCounter.decrementOpeners(player, this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    void playOpenSound(BlockState state) {
        Vec3i off = state.getValue(CofferBlock.FACING).getNormal();
        double x = this.worldPosition.getX() + 0.5 + off.getX()/2.0;
        double y = this.worldPosition.getY() + 0.5 + off.getY()/2.0;
        double z = this.worldPosition.getZ() + 0.5 + off.getZ()/2.0;
        assert this.level != null;
        this.level.playSound(null, x, y, z, SoundRegistry.COFFER_OPEN.get(), SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat());
    }

    void playCloseSound(BlockState state) {
        Vec3i off = state.getValue(CofferBlock.FACING).getNormal();
        double x = this.worldPosition.getX() + 0.5 + off.getX()/2.0;
        double y = this.worldPosition.getY() + 0.5 + off.getY()/2.0;
        double z = this.worldPosition.getZ() + 0.5 + off.getZ()/2.0;
        assert this.level != null;
        this.level.playSound(null, x, y, z, SoundRegistry.COFFER_CLOSE.get(), SoundSource.BLOCKS, 0.5F, this.level.random.nextFloat());
    }

    @Override
    public float getOpenNess(float partialTick) {
        return this.chestLidController.getOpenness(partialTick);
    }

    public static void lidAnimateTick(Level level, BlockPos pos, BlockState state, CofferBlockEntity entity) {
        entity.chestLidController.tickLid();
    }

    @Override
    public boolean triggerEvent(int id, int param) {
        if (id == 1) {
            this.chestLidController.shouldBeOpen(param > 0);
            return true;
        }
        return super.triggerEvent(id, param);
    }

    protected void signalOpenCount(Level level, BlockPos pos, BlockState state, int before, int after) {
        Block block = state.getBlock();
        level.blockEvent(pos, block, 1, after);
    }
}
