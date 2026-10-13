package net.satisfy.furniture.core.block;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.block.entity.BoatInAJarBlockEntity;
import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.foundation.block.FacingBlock;
import net.satisfy.foundation.overlay.BlockNotice;
import net.satisfy.furniture.core.util.GeneralUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class BoatInAJarBlock extends FacingBlock implements EntityBlock {
    public static final BooleanProperty BROKEN = BooleanProperty.create("broken");

    public BoatInAJarBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(BROKEN, false));
    }

    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.3125, 0.9375, 0.125, 0.6875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.3125, 0, 0.25, 0.6875, 0.1875, 0.375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.1875, 0.125, 0.4375, 0.8125, 0.25, 0.5625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0.25, 0.21875, 1, 0.8125, 0.78125), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.006249999999999978, 0.25625, 0.22499999999999998, 0.99375, 0.31875, 0.775), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.5625, 0.3125, 0.28125, 0.875, 0.625, 0.71875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.3125, 0.34375, 0.5625, 0.5, 0.65625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.375, 0.5, 0.34375, 0.375, 0.75, 0.65625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.4375, 0.4375, 0.40625, 0, 0.625, 0.59375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.3125, 0.375, 0.34375, -0.1875, 0.6875, 0.65625), BooleanOp.OR);
        return shape;
    };

    public static final Map<Direction, VoxelShape> SHAPE = GeneralUtil.horizontalShapes(voxelShapeSupplier.get());

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BROKEN);
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (isWaterBottle(stack) && PlatformHelper.isBoatInAJarStormEnabled()) {
            if (!(level.getBlockEntity(pos) instanceof BoatInAJarBlockEntity be) || be.isStorming()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (!level.isClientSide) {
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.GLASS_BOTTLE)));
                level.blockEvent(pos, this, BoatInAJarBlockEntity.STORM_EVENT, 0);
                level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 0.8F, 1.0F);
                level.playSound(null, pos, SoundEvents.WEATHER_RAIN, SoundSource.BLOCKS, 0.3F, 1.6F);
                if (player instanceof ServerPlayer serverPlayer) {
                    BlockNotice.send(serverPlayer, pos, Component.translatable("hud.furniture.boat_in_a_jar.storm"));
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!state.getValue(BROKEN) || !stack.is(Items.STICK)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            stack.consume(1, player);
            level.setBlock(pos, state.setValue(BROKEN, false), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.8F, 1.4F);
            if (player instanceof ServerPlayer serverPlayer) {
                BlockNotice.send(serverPlayer, pos, Component.translatable("hud.furniture.boat_in_a_jar.repaired"));
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!PlatformHelper.isBoatInAJarShakingEnabled()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof BoatInAJarBlockEntity be)) {
            return InteractionResult.PASS;
        }
        float energy = be.shake();
        level.blockEvent(pos, this, BoatInAJarBlockEntity.SHAKE_EVENT, Math.round(energy * 100.0F));
        level.playSound(null, pos, SoundEvents.BOAT_PADDLE_WATER, SoundSource.BLOCKS, 0.25F + energy * 0.08F, 0.8F + energy * 0.12F);
        if (state.getValue(BROKEN)) {
            return InteractionResult.CONSUME;
        }
        if (PlatformHelper.canBoatInAJarBreak() && energy >= BoatInAJarBlockEntity.breakEnergy()) {
            level.setBlock(pos, state.setValue(BROKEN, true), Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0F, 1.3F);
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState()), pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, 12, 0.2, 0.1, 0.1, 0.05);
            }
            if (player instanceof ServerPlayer serverPlayer) {
                BlockNotice.send(serverPlayer, pos, Component.translatable("hud.furniture.boat_in_a_jar.broke").withStyle(ChatFormatting.RED));
            }
        } else if (energy >= BoatInAJarBlockEntity.warnEnergy()) {
            level.playSound(null, pos, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.6F, 1.6F);
        }
        return InteractionResult.CONSUME;
    }

    private static boolean isWaterBottle(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        return stack.is(Items.POTION) && contents != null && contents.is(Potions.WATER);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BoatInAJarBlockEntity(pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && be.triggerEvent(id, param);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return GeneralUtil.ticker(type, EntityTypeRegistry.BOAT_IN_A_JAR_BLOCK_ENTITY.get(), BoatInAJarBlockEntity::tick);
    }
}
