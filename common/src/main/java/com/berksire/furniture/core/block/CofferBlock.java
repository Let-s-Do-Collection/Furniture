package com.berksire.furniture.core.block;

import com.berksire.furniture.core.block.entity.CofferBlockEntity;
import com.berksire.furniture.core.registry.EntityTypeRegistry;
import com.berksire.furniture.core.util.FurnitureUtil;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CofferBlock extends BaseEntityBlock implements SimpleWaterloggedBlock{
	public static final DirectionProperty FACING;
	public static final ResourceLocation CONTENTS;
	public static final BooleanProperty WATERLOGGED;

	public CofferBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED,false));

	}

	public static final MapCodec<CofferBlock> CODEC = simpleCodec(CofferBlock::new);

	@Override
	protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
		VoxelShape shape = Shapes.empty();
		shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.1875, 0.9375, 0.3125, 0.8125), BooleanOp.OR);
		shape = Shapes.join(shape, Shapes.box(0.0625, 0.3125, 0.1875, 0.9375, 0.5, 0.8125), BooleanOp.OR);
		return shape;
	};

	public static final Map<Direction, VoxelShape> SHAPE = net.minecraft.Util.make(new HashMap<>(), map -> {
		for (Direction direction : Direction.Plane.HORIZONTAL.stream().toList()) {
			map.put(direction, FurnitureUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
		}
	});

	@Override
	public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE.get(state.getValue(FACING));
	}

	@Override
	protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof CofferBlockEntity coffer) {
			player.openMenu(coffer);
			return InteractionResult.CONSUME;
		}
		return InteractionResult.PASS;
	}

	@Override
	protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.isClientSide) {
			return ItemInteractionResult.SUCCESS;
		}
		BlockEntity be = level.getBlockEntity(pos);
		if (be instanceof CofferBlockEntity coffer) {
			player.openMenu(coffer);
			return ItemInteractionResult.CONSUME;
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public @NotNull BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
		if (!level.isClientSide) {
			BlockEntity blockEntity = level.getBlockEntity(blockPos);
			if (blockEntity instanceof CofferBlockEntity cofferBlockEntity) {
				ItemStack itemStack = new ItemStack(blockState.getBlock());
				cofferBlockEntity.saveToItem(itemStack, level.registryAccess());
				double x = blockPos.getX() + 0.5;
				double y = blockPos.getY() + 0.5;
				double z = blockPos.getZ() + 0.5;
				ItemEntity itemEntity = new ItemEntity(level, x, y, z, itemStack);
				itemEntity.setDefaultPickUpDelay();
				level.addFreshEntity(itemEntity);
			}
		}
		return super.playerWillDestroy(level, blockPos, blockState, player);
	}

	public @NotNull List<ItemStack> getDrops(BlockState blockState, LootParams.Builder builder) {
		BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
		if (blockEntity instanceof CofferBlockEntity cofferBlockEntity) {
			builder = builder.withDynamicDrop(CONTENTS, (consumer) -> {
				for(int i = 0; i < cofferBlockEntity.getContainerSize(); ++i) {
					consumer.accept(cofferBlockEntity.getItem(i));
				}

			});
		}

		return super.getDrops(blockState, builder);
	}

	public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
		if (itemStack.has(DataComponents.CUSTOM_NAME)) {
			BlockEntity blockEntity = level.getBlockEntity(blockPos);
			if (blockEntity instanceof CofferBlockEntity blockEntity1) {
				blockEntity1.name = itemStack.getHoverName();
			}
		}

	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? createTickerHelper(blockEntityType, EntityTypeRegistry.COFFER_BLOCK_ENTITY.get(), CofferBlockEntity::lidAnimateTick) : null;
	}

	public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
		if (!blockState.is(blockState2.getBlock())) {
			BlockEntity blockEntity = level.getBlockEntity(blockPos);
			if (blockEntity instanceof CofferBlockEntity) {
				level.updateNeighbourForOutputSignal(blockPos, blockState.getBlock());
			}

			super.onRemove(blockState, level, blockPos, blockState2, bl);
		}
	}

	@Override
	public @NotNull ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
		ItemStack itemStack = super.getCloneItemStack(levelReader, blockPos, blockState);
		levelReader.getBlockEntity(blockPos, EntityTypeRegistry.COFFER_BLOCK_ENTITY.get()).ifPresent((cofferBlockEntity) -> cofferBlockEntity.saveToItem(itemStack, levelReader.registryAccess()));
		return itemStack;
	}

	public @NotNull RenderShape getRenderShape(BlockState blockState) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	public boolean hasAnalogOutputSignal(BlockState blockState) {
		return true;
	}

	public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(blockPos));
	}

	public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Nullable
	@Override
	@SuppressWarnings("unused")
	public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
		FluidState fluidState = blockPlaceContext.getLevel().getFluidState(blockPlaceContext.getClickedPos());
		return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new CofferBlockEntity(blockPos,blockState);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	static{
		FACING = HorizontalDirectionalBlock.FACING;
		WATERLOGGED = BlockStateProperties.WATERLOGGED;
		CONTENTS = ResourceLocation.parse("contents");
	}
}