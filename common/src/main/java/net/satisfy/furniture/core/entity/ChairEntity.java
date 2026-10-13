package net.satisfy.furniture.core.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ChairEntity extends Entity {
    private BlockPos seat;
    private BlockPos returnPos;

    public ChairEntity(EntityType<?> type, Level world) {
        super(type, world);
    }

    public void setSeat(BlockPos seat, BlockPos returnPos) {
        this.seat = seat;
        this.returnPos = returnPos;
    }

    public BlockPos getSeat() {
        return seat;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && !isVehicle()) {
            discard();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public @NotNull Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        discard();
        if (passenger instanceof Player && returnPos != null) {
            return new Vec3(returnPos.getX() + 0.5D, returnPos.getY(), returnPos.getZ() + 0.5D);
        }
        return super.getDismountLocationForPassenger(passenger);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {

    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        return new ClientboundAddEntityPacket(this, serverEntity);
    }
}
