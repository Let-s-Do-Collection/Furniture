package net.satisfy.furniture.core.entity;

import net.satisfy.furniture.core.registry.TriggerRegistry;
import net.minecraft.tags.ItemTags;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.registry.ObjectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.satisfy.foundation.overlay.BlockNotice;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class PellsEntity extends Mob {
    private static final EquipmentSlot[] ARMOR_SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    private static final EntityDataAccessor<Float> LAST_DAMAGE = SynchedEntityData.defineId(PellsEntity.class, EntityDataSerializers.FLOAT);
    private static final Predicate<Entity> RIDABLE_MINECARTS = (p_31582_) -> p_31582_ instanceof AbstractMinecart;
    private static final EntityDataAccessor<Boolean> IS_CRIT = SynchedEntityData.defineId(PellsEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> DURABILITY = SynchedEntityData.defineId(PellsEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> HIT_ID = SynchedEntityData.defineId(PellsEntity.class, EntityDataSerializers.INT);

    public static final float REPAIR_HINT_THRESHOLD = 0.10F;
    public static final float LEFT_ARM_THRESHOLD = 0.75F;
    public static final float RIGHT_ARM_THRESHOLD = 0.50F;
    public static final float HEAD_THRESHOLD = 0.25F;
    private static final int POPUP_LIFETIME = 20;
    private static final int COMBO_TIMEOUT = 50;

    public final List<DamagePopup> popups = new ArrayList<>();
    public float comboDamage;
    public int comboHits;
    public int comboAge;
    public float swayX, swayZ, swayXO, swayZO;
    private float swayXVel, swayZVel;
    public float headLag, headLagO;
    private float headLagVel;
    private float hitDirX, hitDirZ = -1.0F;

    public PellsEntity(EntityType<? extends Mob> type, Level worldIn) {
        super(type, worldIn);
        this.setSpeed(0.0F);
    }

    public static @NotNull AttributeSupplier.Builder createMobAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LAST_DAMAGE, 0f);
        builder.define(IS_CRIT, false);
        builder.define(DURABILITY, maxDurability());
        builder.define(HIT_ID, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("Durability", getDurability());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Durability")) {
            entityData.set(DURABILITY, Mth.clamp(tag.getFloat("Durability"), 0.0F, maxDurability()));
        }
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (HIT_ID.equals(key) && level().isClientSide() && entityData.get(HIT_ID) > 0) {
            float amount = getLastDamage();
            boolean crit = isCrit();
            popups.clear();
            popups.add(new DamagePopup(amount, crit, 0.0F, 0.0F));
            comboDamage += amount;
            comboHits++;
            comboAge = 0;
            float strength = Mth.clamp(0.06F + amount * 0.02F, 0.06F, 0.5F) * (crit ? 1.3F : 1.0F);
            swayXVel -= hitDirZ * strength;
            swayZVel += hitDirX * strength;
        }
    }

    public float getDurability() {
        return entityData.get(DURABILITY);
    }

    public static float maxDurability() {
        return PlatformHelper.getPellsMaxDurability();
    }

    public float getDurabilityFraction() {
        return getDurability() / maxDurability();
    }

    public boolean isBroken() {
        return getDurability() <= 0.0F;
    }

    public boolean isCrit() {
        return entityData.get(IS_CRIT);
    }

    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            clientTick();
        }
        if (this.isInWall()) {
            this.spawnAtLocation(ObjectRegistry.PELLS.get());
            this.discard();
        }
    }

    private void clientTick() {
        popups.removeIf(popup -> ++popup.age > POPUP_LIFETIME);
        if (comboHits > 0 && ++comboAge > COMBO_TIMEOUT) {
            comboHits = 0;
            comboDamage = 0.0F;
        }
        swayXO = swayX;
        swayZO = swayZ;
        headLagO = headLag;
        swayXVel = (swayXVel - swayX * 0.28F) * 0.82F;
        swayZVel = (swayZVel - swayZ * 0.28F) * 0.82F;
        swayX += swayXVel;
        swayZ += swayZVel;
        headLagVel = (headLagVel + (swayX - headLag) * 0.35F) * 0.7F;
        headLag += headLagVel;
    }

    @Override
    public void handleDamageEvent(@NotNull DamageSource source) {
        super.handleDamageEvent(source);
        Vec3 from = source.getSourcePosition();
        double dx = from == null ? random.nextGaussian() : this.getX() - from.x;
        double dz = from == null ? random.nextGaussian() : this.getZ() - from.z;
        double length = Math.sqrt(dx * dx + dz * dz);
        if (length < 1.0E-4) {
            return;
        }
        dx /= length;
        dz /= length;
        float yaw = this.getYRot() * Mth.DEG_TO_RAD;
        float sin = Mth.sin(yaw);
        float cos = Mth.cos(yaw);
        float localZ = (float) (dx * sin - dz * cos);
        float localX = (float) (dx * cos + dz * sin);
        boolean crit = source.getEntity() instanceof Player player && player.getAttackStrengthScale(0.5F) > 0.9F && player.fallDistance > 0.0F;
        hitDirX = localX;
        hitDirZ = localZ;
        if (!isBroken()) {
            BlockState hay = Blocks.HAY_BLOCK.defaultBlockState();
            Vec3 hit = getHitPosition(source);
            for (int i = 0; i < (crit ? 18 : 10); i++) {
                level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, hay), hit.x + (random.nextDouble() - 0.5D) * 0.2D, hit.y + (random.nextDouble() - 0.5D) * 0.2D, hit.z + (random.nextDouble() - 0.5D) * 0.2D, dx * 0.15D, 0.1D, dz * 0.15D);
            }
            if (crit) {
                for (int i = 0; i < 6; i++) {
                    level().addParticle(ParticleTypes.CRIT, hit.x, hit.y, hit.z, dx * 0.3D + random.nextGaussian() * 0.1D, 0.2D, dz * 0.3D + random.nextGaussian() * 0.1D);
                }
            }
        }
    }

    private Vec3 getHitPosition(DamageSource source) {
        Vec3 fallback = new Vec3(this.getX(), this.getY() + 1.2D, this.getZ());
        if (!(source.getEntity() instanceof Player player)) {
            return fallback;
        }
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getViewVector(1.0F).scale(player.entityInteractionRange() + 1.0D));
        return this.getBoundingBox().clip(eye, end).orElse(fallback);
    }

    @Override
    public @NotNull InteractionResult mobInteract(Player pPlayer, @NotNull InteractionHand pHand) {
        Level level = pPlayer.level();
        ItemStack held = pPlayer.getItemInHand(pHand);
        if (!pPlayer.isShiftKeyDown() && held.is(Items.HAY_BLOCK) && getDurability() < maxDurability()) {
            if (!level.isClientSide()) {
                entityData.set(DURABILITY, maxDurability());
                held.consume(1, pPlayer);
                level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GRASS_PLACE, SoundSource.NEUTRAL, 0.8F, 0.8F + random.nextFloat() * 0.3F);
                spawnServerParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.HAY_BLOCK.defaultBlockState()), 12, 0.9D);
                spawnServerParticles(ParticleTypes.HAPPY_VILLAGER, 5, 1.2D);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (!pPlayer.isShiftKeyDown()) {
            EquipmentSlot slot = held.isEmpty() ? getWornSlot(pPlayer) : getEquipmentSlotForItem(held);
            if (slot != null && slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR && (held.isEmpty() || canWear(slot))) {
                if (!level.isClientSide()) {
                    ItemStack worn = getItemBySlot(slot);
                    SoundEvent sound = getEquipSound(held.isEmpty() ? worn : held);
                    setItemSlot(slot, held.copyWithCount(1));
                    setGuaranteedDrop(slot);
                    if (!held.isEmpty() && !pPlayer.getAbilities().instabuild) {
                        held.shrink(1);
                    }
                    if (!worn.isEmpty()) {
                        if (pPlayer.getItemInHand(pHand).isEmpty()) {
                            pPlayer.setItemInHand(pHand, worn);
                        } else if (!pPlayer.getInventory().add(worn)) {
                            pPlayer.drop(worn, false);
                        }
                    }
                    level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.NEUTRAL, 1.0F, 1.0F);
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }
        if (pPlayer.isShiftKeyDown() && getDurability() < maxDurability()) {
            if (pPlayer instanceof ServerPlayer serverPlayer) {
                BlockNotice.send(serverPlayer, blockPosition(), Component.translatable("hud.furniture.pells.needs_repair"));
                level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 0.6F, 1.2F);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        if (pPlayer.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                dropArmor();
                if (!pPlayer.isCreative()) {
                    level.addFreshEntity(new ItemEntity(level, this.getX(), this.getY() + 0.7D, this.getZ(), ObjectRegistry.PELLS.get().getDefaultInstance()));
                }
                this.removeAfterChangingDimensions();
            } else {
                for (int i = 0; i < 14; i++) {
                    level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.HAY_BLOCK.defaultBlockState()), this.getX(), this.getY() + 0.7D, this.getZ(), 0d, 0.02d, 0d);
                    level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DARK_OAK_PLANKS.defaultBlockState()), this.getX(), this.getY() + 0.7D, this.getZ(), 0d, 0.02d, 0d);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private EquipmentSlot getWornSlot(Player player) {
        Vec3 eye = player.getEyePosition();
        Vec3 end = eye.add(player.getViewVector(1.0F).scale(player.entityInteractionRange() + 1.0D));
        double height = this.getBoundingBox().clip(eye, end).map(hit -> (hit.y - this.getY()) / this.getBbHeight()).orElse(0.5D);
        EquipmentSlot aimed = height > 0.75D ? EquipmentSlot.HEAD : height > 0.45D ? EquipmentSlot.CHEST : height > 0.2D ? EquipmentSlot.LEGS : EquipmentSlot.FEET;
        if (!getItemBySlot(aimed).isEmpty()) {
            return aimed;
        }
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            if (!getItemBySlot(slot).isEmpty()) {
                return slot;
            }
        }
        return null;
    }

    private boolean canWear(EquipmentSlot slot) {
        float fraction = getDurabilityFraction();
        return slot == EquipmentSlot.HEAD ? fraction > HEAD_THRESHOLD : fraction > 0.0F;
    }

    private SoundEvent getEquipSound(ItemStack stack) {
        Equipable equipable = Equipable.get(stack);
        return equipable != null ? equipable.getEquipSound().value() : SoundEvents.ARMOR_EQUIP_GENERIC.value();
    }

    private void dropArmor() {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            dropArmor(slot);
        }
    }

    private void dropArmor(EquipmentSlot slot) {
        ItemStack stack = getItemBySlot(slot);
        if (!stack.isEmpty()) {
            spawnAtLocation(stack, 1.0F);
            setItemSlot(slot, ItemStack.EMPTY);
        }
    }

    public void kill() {
        this.actuallyHurt(this.level().damageSources().genericKill(), Float.MAX_VALUE);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.level().isClientSide()) {
            return false;
        }
        if (source == this.damageSources().fellOutOfWorld()) {
            this.remove(RemovalReason.KILLED);
            return false;
        }
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            kill();
            return false;
        }
        if (isBroken()) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WOOD_HIT, SoundSource.NEUTRAL, 0.6F, 0.8F);
            return false;
        }
        boolean isCrit = source.getEntity() instanceof Player player && player.getAttackStrengthScale(0.5F) > 0.9F && player.fallDistance > 0.0F && !player.onGround();
        if (!source.is(DamageTypeTags.BYPASSES_ARMOR)) {
            amount = CombatRules.getDamageAfterAbsorb(this, amount, source, getArmorValue(), (float) getAttributeValue(Attributes.ARMOR_TOUGHNESS));
        }
        entityData.set(LAST_DAMAGE, amount);
        entityData.set(IS_CRIT, isCrit);
        entityData.set(HIT_ID, entityData.get(HIT_ID) + 1);
        float before = getDurabilityFraction();
        entityData.set(DURABILITY, Math.max(0.0F, getDurability() - amount));
        float after = getDurabilityFraction();
        dropParts(before, after);
        if (source.getEntity() instanceof ServerPlayer player) {
            if (player.getMainHandItem().is(ItemTags.WOOL)) {
                TriggerRegistry.PELLS_WOOL.get().trigger(player);
            }
            if (before > 0.0F && after <= 0.0F) {
                TriggerRegistry.PELLS_BROKEN.get().trigger(player);
            }
        }
        this.markHurt();
        this.level().broadcastDamageEvent(this, source);
        return true;
    }

    private void dropParts(float before, float after) {
        if (crossed(before, after, LEFT_ARM_THRESHOLD)) {
            dropPart(1.2D, -0.4D, new ItemStack(Items.WHEAT));
        }
        if (crossed(before, after, RIGHT_ARM_THRESHOLD)) {
            dropPart(1.2D, 0.4D, new ItemStack(Items.WHEAT));
        }
        if (crossed(before, after, HEAD_THRESHOLD)) {
            dropPart(1.6D, 0.0D, new ItemStack(Items.WHEAT, 2));
            dropArmor(EquipmentSlot.HEAD);
        }
        if (crossed(before, after, 0.0F)) {
            dropPart(1.0D, 0.0D, new ItemStack(Items.WHEAT, 2));
            dropArmor();
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ARMOR_STAND_BREAK, SoundSource.NEUTRAL, 1.0F, 0.9F);
            spawnServerParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.HAY_BLOCK.defaultBlockState()), 40, 1.0D);
        }
    }

    private static boolean crossed(float before, float after, float threshold) {
        return before > threshold && after <= threshold;
    }

    private void dropPart(double height, double side, ItemStack... stacks) {
        float yaw = this.getYRot() * Mth.DEG_TO_RAD;
        double ox = Mth.cos(yaw) * side;
        double oz = Mth.sin(yaw) * side;
        for (ItemStack stack : stacks) {
            ItemEntity item = new ItemEntity(this.level(), this.getX() + ox, this.getY() + height, this.getZ() + oz, stack);
            item.setDeltaMovement(ox * 0.25D + random.nextGaussian() * 0.05D, 0.2D + random.nextDouble() * 0.1D, oz * 0.25D + random.nextGaussian() * 0.05D);
            item.setDefaultPickUpDelay();
            this.level().addFreshEntity(item);
        }
        this.level().playSound(null, this.getX(), this.getY() + height, this.getZ(), SoundEvents.GRASS_BREAK, SoundSource.NEUTRAL, 0.9F, 0.8F + random.nextFloat() * 0.3F);
        spawnServerParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.HAY_BLOCK.defaultBlockState()), 20, height);
    }

    private void spawnServerParticles(ParticleOptions particle, int count, double height) {
        if (this.level() instanceof ServerLevel server) {
            server.sendParticles(particle, this.getX(), this.getY() + height, this.getZ(), count, 0.25D, 0.25D, 0.25D, 0.05D);
        }
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return isBroken() ? null : SoundEvents.GRASS_HIT;
    }

    public float getLastDamage() {
        return entityData.get(LAST_DAMAGE);
    }

    public boolean isPushable() {
        return false;
    }

    protected void doPush(@NotNull Entity pEntity) {
    }

    protected void pushEntities() {
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), RIDABLE_MINECARTS);
        for (Entity entity : list) {
            if (this.distanceToSqr(entity) <= 0.2D) {
                entity.push(this);
            }
        }
    }

    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = this.getBoundingBox().getSize() * 4.0D;
        if (Double.isNaN(d0) || d0 == 0.0D) {
            d0 = 4.0D;
        }
        d0 *= 64.0D;
        return pDistance < d0 * d0;
    }

    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    public boolean isPickable() {
        return true;
    }

    public boolean isAffectedByPotions() {
        return false;
    }

    public boolean attackable() {
        return false;
    }

    public boolean canBeSeenAsEnemy() {
        return false;
    }

    public boolean canBeSeenByAnyone() {
        return false;
    }

    protected float tickHeadTurn(float pYRot, float pAnimStep) {
        this.yBodyRotO = this.yRotO;
        this.yBodyRot = this.getYRot();
        return 0.0F;
    }

    public @NotNull PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public boolean skipAttackInteraction(@NotNull Entity pEntity) {
        return pEntity instanceof Player && !this.level().mayInteract((Player) pEntity, this.blockPosition());
    }

    public @NotNull Vec3 getLightProbePosition(float pPartialTicks) {
        return super.getLightProbePosition(pPartialTicks);
    }

    @Override
    public void aiStep() {
        super.aiStep();
    }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected boolean isImmobile() {
        return true;
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos) {
    }

    public static class DamagePopup {
        public final float amount;
        public final boolean crit;
        public final float offsetX;
        public final float offsetZ;
        public int age;

        public DamagePopup(float amount, boolean crit, float offsetX, float offsetZ) {
            this.amount = amount;
            this.crit = crit;
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
        }

        public float progress(float partialTicks) {
            return Mth.clamp((age + partialTicks) / POPUP_LIFETIME, 0.0F, 1.0F);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }
}