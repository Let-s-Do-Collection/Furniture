package net.satisfy.furniture.core.block.entity;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.FishTankBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.Direction;
import java.util.UUID;
import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FishTankBlockEntity extends BlockEntity {
    private boolean hasCod;
    private boolean hasPufferfish;
    private boolean hasSalmon;

    private static final int WATCH_DURATION = 400;

    @Nullable
    private UUID watchingCat;
    private int watchTicks;
    @Nullable
    private BlockPos watchSpot;

    public AnimationState idleAnimationState;
    private int idleAnimationTimeout = 0;

    public FishTankBlockEntity(BlockPos pos, BlockState state) {
        super(EntityTypeRegistry.FISH_TANK_BLOCK_ENTITY.get(), pos, state);
        idleAnimationState = new AnimationState();
    }

    public boolean hasCod() {
        return hasCod;
    }

    public void setHasCod(boolean hasCod) {
        this.hasCod = hasCod;
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean hasPufferfish() {
        return hasPufferfish;
    }

    public void setHasPufferfish(boolean hasPufferfish) {
        this.hasPufferfish = hasPufferfish;
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public boolean hasSalmon() {
        return hasSalmon;
    }

    public void setHasSalmon(boolean hasSalmon) {
        this.hasSalmon = hasSalmon;
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        hasCod = tag.getBoolean("HasCod");
        hasPufferfish = tag.getBoolean("HasPufferfish");
        hasSalmon = tag.getBoolean("HasSalmon");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean("HasCod", hasCod);
        tag.putBoolean("HasPufferfish", hasPufferfish);
        tag.putBoolean("HasSalmon", hasSalmon);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FishTankBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (be.watchingCat != null) {
            be.tickWatchingCat(serverLevel, state);
            return;
        }
        if (!PlatformHelper.isFishTankCatWatchingEnabled() || !be.hasAnyFish() || level.getGameTime() % 100 != 0 || level.random.nextInt(3) != 0) {
            return;
        }
        Vec3 center = be.tankCenter(state);
        Cat cat = level.getNearestEntity(Cat.class, TargetingConditions.forNonCombat().ignoreLineOfSight().selector(c -> !((Cat) c).isOrderedToSit() && !((Cat) c).isPassenger() && !((Cat) c).isLeashed()), null, center.x, center.y, center.z, new AABB(pos).inflate(10.0));
        if (cat == null) {
            return;
        }
        BlockPos spot = be.findWatchSpot(level, state, cat);
        if (spot == null) {
            return;
        }
        be.watchingCat = cat.getUUID();
        be.watchSpot = spot;
        be.watchTicks = WATCH_DURATION;
    }

    private void tickWatchingCat(ServerLevel level, BlockState state) {
        Cat cat = level.getEntity(watchingCat) instanceof Cat c ? c : null;
        if (cat == null || watchSpot == null || --watchTicks <= 0 || cat.isOrderedToSit() || cat.isLeashed() || !hasAnyFish()) {
            if (cat != null && !cat.isOrderedToSit()) {
                cat.setInSittingPose(false);
            }
            watchingCat = null;
            watchSpot = null;
            return;
        }
        Vec3 center = tankCenter(state);
        if (cat.distanceToSqr(Vec3.atBottomCenterOf(watchSpot)) > 1.0) {
            if (watchTicks % 10 == 0) {
                cat.getNavigation().moveTo(watchSpot.getX() + 0.5, watchSpot.getY(), watchSpot.getZ() + 0.5, 0.9);
            }
            if (watchTicks < WATCH_DURATION / 2 && !cat.isInSittingPose()) {
                watchTicks = 0;
            }
            return;
        }
        cat.getNavigation().stop();
        cat.setInSittingPose(true);
        cat.getLookControl().setLookAt(center.x, center.y, center.z, 30.0F, 30.0F);
        if (level.random.nextInt(120) == 0) {
            level.playSound(null, cat.blockPosition(), SoundEvents.CAT_AMBIENT, SoundSource.NEUTRAL, 0.6F, 1.0F);
        }
    }

    private boolean hasAnyFish() {
        return hasCod || hasPufferfish || hasSalmon;
    }

    private Vec3 tankCenter(BlockState state) {
        BlockPos head = worldPosition.relative(state.getValue(FishTankBlock.FACING));
        return new Vec3((worldPosition.getX() + head.getX()) / 2.0 + 0.5, worldPosition.getY() + 0.6, (worldPosition.getZ() + head.getZ()) / 2.0 + 0.5);
    }

    @Nullable
    private BlockPos findWatchSpot(Level level, BlockState state, Cat cat) {
        Direction facing = state.getValue(FishTankBlock.FACING);
        BlockPos head = worldPosition.relative(facing);
        BlockPos best = null;
        double bestDistance = Double.MAX_VALUE;
        for (BlockPos part : new BlockPos[]{worldPosition, head}) {
            for (Direction side : new Direction[]{facing.getClockWise(), facing.getCounterClockWise()}) {
                BlockPos candidate = part.relative(side);
                if (!level.getBlockState(candidate).getCollisionShape(level, candidate).isEmpty() || level.getBlockState(candidate.below()).getCollisionShape(level, candidate.below()).isEmpty()) {
                    continue;
                }
                double distance = cat.distanceToSqr(Vec3.atBottomCenterOf(candidate));
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = candidate;
                }
            }
        }
        return best;
    }

    public void updateAnimations() {
        if (idleAnimationTimeout <= 0) {
            idleAnimationTimeout = 123;
            idleAnimationState.start(0);
        } else {
            idleAnimationTimeout--;
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }
}
