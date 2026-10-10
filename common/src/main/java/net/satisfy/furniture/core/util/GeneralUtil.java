package net.satisfy.furniture.core.util;

import net.satisfy.foundation.block.VerticalConnectingType;
import net.satisfy.furniture.core.entity.ChairEntity;
import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.satisfy.foundation.registry.FoundationParticles;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class GeneralUtil {
    public static <T extends Block> RegistrySupplier<T> registerWithItem(DeferredRegister<Block> registerB, Registrar<Block> registrarB, DeferredRegister<Item> registerI, Registrar<Item> registrarI, ResourceLocation name, Supplier<T> block) {
        RegistrySupplier<T> toReturn = registerWithoutItem(registerB, registrarB, name, block);
        registerItem(registerI, registrarI, name, () -> new BlockItem(toReturn.get(), new Item.Properties()));
        return toReturn;
    }

    public static <T extends Block> RegistrySupplier<T> registerWithoutItem(DeferredRegister<Block> register, Registrar<Block> registrar, ResourceLocation path, Supplier<T> block) {
        return Platform.isNeoForge() ? register.register(path.getPath(), block) : registrar.register(path, block);
    }

    public static <T extends Item> RegistrySupplier<T> registerItem(DeferredRegister<Item> register, Registrar<Item> registrar, ResourceLocation path, Supplier<T> itemSupplier) {
        return Platform.isNeoForge() ? register.register(path.getPath(), itemSupplier) : registrar.register(path, itemSupplier);
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;

        for (int i = 0; i < times; ++i) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.joinUnoptimized(buffer[1], Shapes.box(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX), BooleanOp.OR));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> ticker(BlockEntityType<A> actual, BlockEntityType<E> expected, BlockEntityTicker<? super E> ticker) {
        return expected == actual ? (BlockEntityTicker<A>) ticker : null;
    }

    public static Map<Direction, VoxelShape> horizontalShapes(VoxelShape north) {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            shapes.put(direction, rotateShape(Direction.NORTH, direction, north).optimize());
        }
        return shapes;
    }

    public static Optional<Tuple<Float, Float>> getRelativeHitCoordinatesForBlockFace(
            BlockHitResult blockHitResult,
            Direction direction,
            Direction[] unAllowedDirections) {

        Direction hitDirection = blockHitResult.getDirection();

        for (Direction unAllowed : unAllowedDirections) {
            if (unAllowed == hitDirection) {
                return Optional.empty();
            }
        }

        if (hitDirection != direction && hitDirection != Direction.UP && hitDirection != Direction.DOWN) {
            return Optional.empty();
        }

        BlockPos adjacentPos = blockHitResult.getBlockPos().relative(hitDirection);
        Vec3 hitLocation = blockHitResult.getLocation().subtract(
                adjacentPos.getX(),
                adjacentPos.getY(),
                adjacentPos.getZ()
        );

        float x = (float) hitLocation.x();
        float z = (float) hitLocation.z();
        float y = (float) hitLocation.y();

        Direction effectiveDirection = (hitDirection == Direction.UP || hitDirection == Direction.DOWN)
                ? direction
                : hitDirection;

        return switch (effectiveDirection) {
            case NORTH -> Optional.of(new Tuple<>(1.0f - x, y));
            case SOUTH -> Optional.of(new Tuple<>(x, y));
            case WEST -> Optional.of(new Tuple<>(z, y));
            case EAST -> Optional.of(new Tuple<>(1.0f - z, y));
            default -> Optional.empty();
        };
    }

    public static ItemInteractionResult useItemOn(Level world, Player player, InteractionHand hand, BlockHitResult hit, double extraHeight) {
        if (world.isClientSide) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (player.isShiftKeyDown()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (player.getVehicle() instanceof ChairEntity) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (hit.getDirection() == Direction.DOWN) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        BlockPos hitPos = hit.getBlockPos();
        if (player.getItemInHand(hand).isEmpty() && getChairs(world, hitPos).isEmpty()) {
            ChairEntity chair = EntityTypeRegistry.CHAIR.get().create(world);
            if (chair == null) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            chair.setSeat(hitPos, player.blockPosition());
            chair.moveTo(hitPos.getX() + 0.5D, hitPos.getY() + 0.25D + extraHeight, hitPos.getZ() + 0.5D, 0, 0);
            world.addFreshEntity(chair);
            player.startRiding(chair);
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static void onStateReplaced(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            for (ChairEntity chair : getChairs(world, pos)) {
                chair.ejectPassengers();
                chair.discard();
            }
        }
    }

    private static List<ChairEntity> getChairs(Level world, BlockPos pos) {
        return world.getEntitiesOfClass(ChairEntity.class, new AABB(pos).expandTowards(0, 1, 0), chair -> pos.equals(chair.getSeat()));
    }

    public static final EnumProperty<VerticalConnectingType> VERTICAL_CONNECTING_TYPE = EnumProperty.create("type", VerticalConnectingType.class);

    public enum SmokeType implements StringRepresentable {
        HEAVY_SMOKE("heavy_smoke"),
        SMOKE("smoke"),
        NO_SMOKE("no_smoke");

        private final String name;

        SmokeType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        public SmokeType getNext() {
            return switch (this) {
                case HEAVY_SMOKE -> SMOKE;
                case SMOKE -> NO_SMOKE;
                default -> HEAVY_SMOKE;
            };
        }

        public String getTranslationKey() {
            return "tooltip.furniture.smoke_type." + this.name;
        }
    }

    public static Collection<ServerPlayer> tracking(ServerLevel world, ChunkPos pos) {
        Objects.requireNonNull(world, "The world cannot be null");
        Objects.requireNonNull(pos, "The chunk pos cannot be null");
        return world.getChunkSource().chunkMap.getPlayers(pos, false);
    }

    public static Collection<ServerPlayer> tracking(ServerLevel world, BlockPos pos) {
        Objects.requireNonNull(pos, "BlockPos cannot be null");
        return tracking(world, new ChunkPos(pos));
    }

    public static void spawnFeathers(Level level, BlockPos pos, Entity entity, float fallDistance, DyeColor color) {
        if (!(level instanceof ServerLevel serverLevel) || fallDistance < 0.5F) {
            return;
        }
        int count = Math.min(12, 2 + (int) (fallDistance * 2.0F));
        ColorParticleOption option = ColorParticleOption.create(FoundationParticles.FEATHER.get(), color.getTextureDiffuseColor());
        serverLevel.sendParticles(option, entity.getX(), pos.getY() + 0.6, entity.getZ(), count, 0.3, 0.1, 0.3, 0.06);
    }
}
