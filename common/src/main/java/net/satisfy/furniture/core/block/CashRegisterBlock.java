package net.satisfy.furniture.core.block;

import net.satisfy.furniture.core.registry.TriggerRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.foundation.block.FacingBlock;
import net.satisfy.furniture.core.util.GeneralUtil;
import net.satisfy.furniture.core.block.entity.CashRegisterBlockEntity;
import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.satisfy.furniture.core.registry.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.satisfy.furniture.core.registry.SoundRegistry;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public class CashRegisterBlock extends FacingBlock implements EntityBlock {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;


    public CashRegisterBlock(Properties settings) {
        super(settings);
        registerDefaultState(defaultBlockState().setValue(OPEN, false).setValue(POWERED, false));
    }

    private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.1875, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.1875, 0.25, 0.875, 0.375, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.375, 0.3125, 0.875, 0.5625, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0.5625, 0.5625, 0.875, 0.875, 0.875), BooleanOp.OR);
        return shape;
    };

    public static final Map<Direction, VoxelShape> SHAPE = GeneralUtil.horizontalShapes(voxelShapeSupplier.get());

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN, POWERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE.get(state.getValue(FACING));
    }

    @Override
    protected boolean isPathfindable(BlockState blockState, PathComputationType pathComputationType) {
        return false;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(TagRegistry.CASH_REGISTER_VALUABLES)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        CashRegisterBlockEntity be = getOrCreateBlockEntity(level, pos, state);
        if (be == null || !be.isOpen()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            int slot = findSlotFor(be, stack, compartmentAt(state, pos, hit.getLocation()));
            if (slot < 0) {
                return ItemInteractionResult.CONSUME;
            }
            ItemStack existing = be.getItem(slot);
            int moved = existing.isEmpty() ? stack.getCount() : Math.min(stack.getCount(), existing.getMaxStackSize() - existing.getCount());
            ItemStack result = existing.isEmpty() ? stack.copyWithCount(moved) : existing.copyWithCount(existing.getCount() + moved);
            stack.consume(moved, player);
            be.setItem(slot, result);
            level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.6F, 1.6F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        CashRegisterBlockEntity be = getOrCreateBlockEntity(level, pos, state);
        if (be == null) {
            return InteractionResult.PASS;
        }
        if (player.isSecondaryUseActive()) {
            if (be.isOpen()) {
                close(level, pos, be);
            } else {
                open(level, pos, be);
            }
            return InteractionResult.CONSUME;
        }
        if (!be.isOpen() || !player.getMainHandItem().isEmpty()) {
            Villager merchant = findMerchant((ServerLevel) level, pos);
            if (merchant != null) {
                chime(level, pos);
                merchant.mobInteract(player, InteractionHand.MAIN_HAND);
                if (player instanceof ServerPlayer serverPlayer) {
                    TriggerRegistry.CASH_REGISTER_TRADE.get().trigger(serverPlayer);
                }
            } else if (!be.isOpen()) {
                open(level, pos, be);
            }
            return InteractionResult.CONSUME;
        }
        int slot = compartmentAt(state, pos, hit.getLocation());
        ItemStack taken = be.getItem(slot);
        if (taken.isEmpty()) {
            close(level, pos, be);
            return InteractionResult.CONSUME;
        }
        be.setItem(slot, ItemStack.EMPTY);
        if (!player.getInventory().add(taken)) {
            player.drop(taken, false);
        }
        level.playSound(null, pos, SoundEvents.CHAIN_PLACE, SoundSource.BLOCKS, 0.6F, 1.3F);
        return InteractionResult.CONSUME;
    }

    public static void open(Level level, BlockPos pos, CashRegisterBlockEntity be) {
        if (be.isOpen()) {
            be.touch();
            return;
        }
        be.setOpen(true);
        chime(level, pos);
    }

    private static void chime(Level level, BlockPos pos) {
        level.playSound(null, pos, SoundRegistry.CASH_REGISTER.get(), SoundSource.BLOCKS, 0.3F, 1.4F);
    }

    public static void close(Level level, BlockPos pos, CashRegisterBlockEntity be) {
        if (be.isOpen()) {
            be.setOpen(false);
            level.playSound(null, pos, SoundEvents.CHISELED_BOOKSHELF_INSERT, SoundSource.BLOCKS, 0.8F, 0.8F);
        }
    }

    public static int compartmentAt(BlockState state, BlockPos pos, Vec3 location) {
        Direction right = state.getValue(FACING).getCounterClockWise();
        Vec3 local = location.subtract(Vec3.atCenterOf(pos));
        double along = local.x * right.getStepX() + local.z * right.getStepZ() + 0.5;
        return Mth.clamp((int) ((along * 16.0 - 4.0) / (8.0 / CashRegisterBlockEntity.SLOTS)), 0, CashRegisterBlockEntity.SLOTS - 1);
    }

    private static int findSlotFor(CashRegisterBlockEntity be, ItemStack stack, int preferred) {
        if (fits(be.getItem(preferred), stack)) {
            return preferred;
        }
        for (int i = 0; i < CashRegisterBlockEntity.SLOTS; i++) {
            if (!be.getItem(i).isEmpty() && fits(be.getItem(i), stack)) {
                return i;
            }
        }
        for (int i = 0; i < CashRegisterBlockEntity.SLOTS; i++) {
            if (be.getItem(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private static boolean fits(ItemStack existing, ItemStack stack) {
        return existing.isEmpty() || ItemStack.isSameItemSameComponents(existing, stack) && existing.getCount() < existing.getMaxStackSize();
    }

    @Nullable
    private static Villager findMerchant(ServerLevel level, BlockPos pos) {
        PoiManager poi = level.getPoiManager();
        List<BlockPos> candidates = new ArrayList<>();
        candidates.add(pos.below());
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            candidates.add(pos.relative(direction));
            candidates.add(pos.relative(direction).below());
        }
        double merchantRange = PlatformHelper.getCashRegisterMerchantRange();
        for (BlockPos station : candidates) {
            if (!poi.getType(station).map(type -> type.is(PoiTypeTags.ACQUIRABLE_JOB_SITE)).orElse(false)) {
                continue;
            }
            GlobalPos site = GlobalPos.of(level.dimension(), station);
            Vec3 center = Vec3.atCenterOf(station);
            Optional<Villager> villager = level.getEntitiesOfClass(Villager.class, new AABB(station).inflate(merchantRange), v ->
                    v.isAlive() && !v.isBaby() && !v.isSleeping() && !v.isTrading()
                            && v.position().distanceTo(center) <= merchantRange
                            && v.getBrain().getMemory(MemoryModuleType.JOB_SITE).map(site::equals).orElse(false)
                            && !v.getOffers().isEmpty()).stream().findFirst();
            if (villager.isPresent()) {
                return villager.get();
            }
        }
        return null;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
        boolean powered = level.hasNeighborSignal(pos);
        if (powered == state.getValue(POWERED) || level.isClientSide) {
            return;
        }
        level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
        if (powered) {
            CashRegisterBlockEntity be = getOrCreateBlockEntity(level, pos, state);
            if (be != null) {
                open(level, pos, be);
            }
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof CashRegisterBlockEntity be ? be.getAnalogSignal() : 0;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof CashRegisterBlockEntity be) {
            Containers.dropContents(level, pos, be.getItems());
            level.updateNeighbourForOutputSignal(pos, this);
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CashRegisterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : GeneralUtil.ticker(type, EntityTypeRegistry.CASH_REGISTER_BLOCK_ENTITY.get(), CashRegisterBlockEntity::serverTick);
    }

    @Nullable
    private static CashRegisterBlockEntity getOrCreateBlockEntity(Level level, BlockPos pos, BlockState state) {
        if (level.getBlockEntity(pos) instanceof CashRegisterBlockEntity be) {
            return be;
        }
        if (level.isClientSide) {
            return null;
        }
        CashRegisterBlockEntity be = new CashRegisterBlockEntity(pos, state);
        level.setBlockEntity(be);
        return be;
    }
}
