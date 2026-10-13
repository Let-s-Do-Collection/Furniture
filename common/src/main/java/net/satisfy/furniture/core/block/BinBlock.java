package net.satisfy.furniture.core.block;

import net.satisfy.furniture.core.registry.TriggerRegistry;
import net.satisfy.furniture.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;
import net.satisfy.foundation.registry.FoundationParticles;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.satisfy.foundation.overlay.BlockNotice;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.furniture.core.registry.ObjectRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BinBlock extends Block {
    public static final int MAX_FILL_STAGE = 11;
    private static final int STINK_STAGE = 8;
    private static final int FLY_STAGE = 10;
    private static final int STINK_COLOR = 0xFF7A8F3A;
    public static final IntegerProperty FILL_STAGE = IntegerProperty.create("fill_stage", 0, MAX_FILL_STAGE);
    public static final BooleanProperty DECORATIVE = BooleanProperty.create("decorative");
    private static final VoxelShape SHAPE = Shapes.box(2.0 / 16.0, 0.0, 2.0 / 16.0, 14.0 / 16.0, 15.0 / 16.0, 14.0 / 16.0);
    private static final Random RANDOM = new Random();

    public BinBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FILL_STAGE, 0).setValue(DECORATIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FILL_STAGE, DECORATIVE);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (world.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty()) {
            recycle(heldItem, world, pos, player);
            int currentFillStage = state.getValue(FILL_STAGE);

            if (currentFillStage < 11) {
                world.setBlock(pos, state.setValue(FILL_STAGE, currentFillStage + 1), 3);
                world.playSound(null, pos, SoundEvents.COMPOSTER_FILL_SUCCESS, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (currentFillStage >= 9 && !state.getValue(DECORATIVE)) {
                    spawnParticles(world, pos);
                }
                if (currentFillStage + 1 == FLY_STAGE && player instanceof ServerPlayer serverPlayer) {
                    TriggerRegistry.BIN_FLIES.get().trigger(serverPlayer);
                }
            } else {
                world.playSound(null, pos, SoundEvents.COMPOSTER_READY, SoundSource.BLOCKS, 1.0F, 1.0F);
                boolean bagFound = RANDOM.nextInt(100) < PlatformHelper.getBinBagDropChance();
                if (bagFound) {
                    dropItem(world, pos);
                }
                if (player instanceof ServerPlayer serverPlayer) {
                    BlockNotice.send(serverPlayer, pos, Component.translatable(bagFound ? "hud.furniture.bin.bag_found" : "hud.furniture.bin.emptied"));
                }
                world.setBlock(pos, state.setValue(FILL_STAGE, 0), 3);
            }
            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult blockHitResult) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (!world.isClientSide) {
            boolean decorative = !state.getValue(DECORATIVE);
            world.setBlock(pos, state.setValue(DECORATIVE, decorative), 3);
            world.playSound(null, pos, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.6F, 1.2F);
            if (player instanceof ServerPlayer serverPlayer) {
                BlockNotice.send(serverPlayer, pos, Component.translatable(decorative ? "hud.furniture.bin.decorative_on" : "hud.furniture.bin.decorative_off"));
            }
        }
        return InteractionResult.sidedSuccess(world.isClientSide);
    }

    private void recycle(ItemStack stack, Level world, BlockPos pos, Player player) {
        Item nugget = recyclingNugget(stack.getItem());
        if (nugget == null) {
            return;
        }
        float condition = stack.isDamageableItem() ? 1.0F - (float) stack.getDamageValue() / stack.getMaxDamage() : 1.0F;
        int count = 1 + Math.round(condition * 3.0F);
        ItemStack nuggets = new ItemStack(nugget, count);
        Block.popResource(world, pos.above(), nuggets);
        world.playSound(null, pos, SoundEvents.ANVIL_LAND, SoundSource.BLOCKS, 0.3F, 1.6F);
        if (player instanceof ServerPlayer serverPlayer) {
            BlockNotice.send(serverPlayer, pos, Component.translatable("hud.furniture.bin.recycled", count, nuggets.getHoverName()));
        }
    }

    @Nullable
    private static Item recyclingNugget(Item item) {
        if (item instanceof TieredItem tiered) {
            if (tiered.getTier() == Tiers.IRON) return Items.IRON_NUGGET;
            if (tiered.getTier() == Tiers.GOLD) return Items.GOLD_NUGGET;
        }
        if (item instanceof ArmorItem armor) {
            if (armor.getMaterial().value() == ArmorMaterials.IRON.value() || armor.getMaterial().value() == ArmorMaterials.CHAIN.value()) return Items.IRON_NUGGET;
            if (armor.getMaterial().value() == ArmorMaterials.GOLD.value()) return Items.GOLD_NUGGET;
        }
        return null;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return Math.round(state.getValue(FILL_STAGE) * 15.0F / MAX_FILL_STAGE);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int stage = state.getValue(FILL_STAGE);
        if (stage < STINK_STAGE || state.getValue(DECORATIVE)) {
            return;
        }
        if (PlatformHelper.isBinStinkEnabled() && random.nextInt(4) == 0) {
            level.addParticle(ColorParticleOption.create(FoundationParticles.COLORED_STEAM.get(), STINK_COLOR),
                    pos.getX() + 0.3 + random.nextDouble() * 0.4, pos.getY() + 0.95, pos.getZ() + 0.3 + random.nextDouble() * 0.4,
                    0.0, 0.02, 0.0);
        }
        if (!PlatformHelper.areBinFliesEnabled()) {
            return;
        }
        if (stage >= FLY_STAGE && random.nextInt(PlatformHelper.getBinFlyFrequency()) == 0) {
            level.addParticle(FoundationParticles.FLY.get(), pos.getX() + 0.5, pos.getY() + 1.15 + random.nextDouble() * 0.2, pos.getZ() + 0.5, PlatformHelper.getBinFlyLifetime(), 0.0, 0.0);
        }
        if (stage >= FLY_STAGE && random.nextInt(60) == 0) {
            level.playLocalSound(pos, SoundEvents.BEE_LOOP, SoundSource.BLOCKS, 0.15F, 1.8F + random.nextFloat() * 0.2F, false);
        }
    }

    private void dropItem(Level world, BlockPos pos) {
        ItemStack droppedItem = new ItemStack(ObjectRegistry.TRASH_BAG.get());
        Block.popResource(world, pos, droppedItem);
    }

    private void spawnParticles(Level world, BlockPos pos) {
        for (int i = 0; i < 20; i++) {
            double offsetX = RANDOM.nextDouble() * 0.6 - 0.3;
            double offsetY = RANDOM.nextDouble() * 0.6 - 0.3;
            double offsetZ = RANDOM.nextDouble() * 0.6 - 0.3;
            world.addParticle(ParticleTypes.SMOKE, pos.getX() + 0.5 + offsetX, pos.getY() + 1.0 + offsetY, pos.getZ() + 0.5 + offsetZ, 0.0, 1, 0.0);
        }
    }
}
