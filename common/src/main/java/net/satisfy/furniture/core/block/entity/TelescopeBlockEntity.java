package net.satisfy.furniture.core.block.entity;

import net.minecraft.util.Mth;
import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.TelescopeBlock;
import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class TelescopeBlockEntity extends BlockEntity {
    public static final int RETURN_TICKS = 12;

    @Nullable
    private UUID user;
    private int returnTicks;

    public float renderYaw;
    public float renderPitch;
    public long lastRenderNanos;
    public long inactiveSinceNanos = 1L;

    public TelescopeBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.TELESCOPE_BLOCK_ENTITY.get(), pos, state);
    }

    @Nullable
    public UUID getUser() {
        return user;
    }

    public void setUser(@Nullable UUID user) {
        if (level == null || level.isClientSide || java.util.Objects.equals(this.user, user)) {
            return;
        }
        this.user = user;
        if (user != null) {
            returnTicks = 0;
            setActive(true);
        } else {
            returnTicks = RETURN_TICKS;
        }
        setChanged();
        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_CLIENTS);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TelescopeBlockEntity be) {
        if (be.user != null) {
            Player player = level.getPlayerByUUID(be.user);
            if (player == null || player.position().distanceToSqr(Vec3.atCenterOf(pos)) > Mth.square(PlatformHelper.getTelescopeMaxDistance() + 1.0)) {
                be.setUser(null);
            }
        } else if (be.returnTicks > 0 && --be.returnTicks == 0) {
            be.setActive(false);
        } else if (be.returnTicks == 0 && state.getValue(TelescopeBlock.ACTIVE)) {
            be.setActive(false);
        }
    }

    private void setActive(boolean active) {
        if (level == null) {
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        if (state.getBlock() instanceof TelescopeBlock && state.getValue(TelescopeBlock.ACTIVE) != active) {
            level.setBlock(worldPosition, state.setValue(TelescopeBlock.ACTIVE, active), Block.UPDATE_ALL);
            BlockPos below = worldPosition.below();
            BlockState lower = level.getBlockState(below);
            if (lower.getBlock() instanceof TelescopeBlock) {
                level.setBlock(below, lower.setValue(TelescopeBlock.ACTIVE, active), Block.UPDATE_ALL);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (user != null) {
            tag.putUUID("User", user);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        user = tag.hasUUID("User") ? tag.getUUID("User") : null;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }
}
