package com.berksire.furniture.core.registry;

import com.berksire.furniture.Furniture;
import com.berksire.furniture.core.block.entity.CabinetBlockEntity;
import com.berksire.furniture.core.block.entity.ChimneyBlockEntity;
import com.berksire.furniture.core.block.entity.ClockBlockEntity;
import com.berksire.furniture.core.block.entity.CofferBlockEntity;
import com.berksire.furniture.core.block.entity.DisplayBlockEntity;
import com.berksire.furniture.core.block.entity.DresserBlockEntity;
import com.berksire.furniture.core.block.entity.FishTankBlockEntity;
import com.berksire.furniture.core.block.entity.GramophoneBlockEntity;
import com.berksire.furniture.core.block.entity.GrandfatherClockBlockEntity;
import com.berksire.furniture.core.entity.CanvasEntity;
import com.berksire.furniture.core.entity.ChairEntity;
import com.berksire.furniture.core.entity.PellsEntity;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Map;
import java.util.function.Supplier;

import static com.berksire.furniture.core.registry.ObjectRegistry.*;

public final class EntityTypeRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Furniture.MOD_ID, Registries.ENTITY_TYPE);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Furniture.MOD_ID, Registries.BLOCK_ENTITY_TYPE);
    private static final Registrar<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTRAR = BLOCK_ENTITY_TYPES.getRegistrar();

    public static final RegistrySupplier<BlockEntityType<GrandfatherClockBlockEntity>> GRANDFATHER_CLOCK_BLOCK_ENTITY = registerBlockEntity("grandfather_clock", () -> BlockEntityType.Builder.of(GrandfatherClockBlockEntity::new, toBlocks(GRANDFATHER_CLOCKS)).build(null));
    public static final RegistrySupplier<BlockEntityType<ClockBlockEntity>> CLOCK_BLOCK_ENTITY = registerBlockEntity("clock", () -> BlockEntityType.Builder.of(ClockBlockEntity::new, toBlocks(CLOCKS)).build(null));
    public static final RegistrySupplier<BlockEntityType<CofferBlockEntity>> COFFER_BLOCK_ENTITY = registerBlockEntity("coffer", () -> BlockEntityType.Builder.of(CofferBlockEntity::new, COFFER.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<CabinetBlockEntity>> CABINET_BLOCK_ENTITY = registerBlockEntity("cabinet", () -> BlockEntityType.Builder.of(CabinetBlockEntity::new, toBlocks(CABINETS)).build(null));
    public static final RegistrySupplier<BlockEntityType<FishTankBlockEntity>> FISH_TANK_BLOCK_ENTITY = registerBlockEntity("fish_tank", () -> BlockEntityType.Builder.of(FishTankBlockEntity::new, COPPER_FISH_TANK.get(), IRON_FISH_TANK.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<GramophoneBlockEntity>> GRAMOPHONE_BLOCK_ENTITY = registerBlockEntity("gramophone", () -> BlockEntityType.Builder.of(GramophoneBlockEntity::new, GRAMOPHONE.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<ChimneyBlockEntity>> CHIMNEY_BLOCK_ENTITY = registerBlockEntity("chimney", () -> BlockEntityType.Builder.of(ChimneyBlockEntity::new, COPPER_CHIMNEY.get(), STONE_BRICKS_CHIMNEY.get(), BRICK_CHIMNEY.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<DresserBlockEntity>> DRESSER_BLOCK_ENTITY = registerBlockEntity("dresser", () -> BlockEntityType.Builder.of(DresserBlockEntity::new, toBlocks(DRESSER)).build(null));
    public static final RegistrySupplier<BlockEntityType<DisplayBlockEntity>> DISPLAY_BLOCK_ENTITY = registerBlockEntity("display", () -> BlockEntityType.Builder.of(DisplayBlockEntity::new, DISPLAY.get()).build(null));

    public static final RegistrySupplier<EntityType<ChairEntity>> CHAIR = registerEntity("chair", () -> EntityType.Builder.of(ChairEntity::new, MobCategory.MISC).sized(0.001F, 0.001F).build(Furniture.identifier("chair").toString()));
    public static final RegistrySupplier<EntityType<CanvasEntity>> CANVAS = registerEntity("canvas", () -> EntityType.Builder.<CanvasEntity>of(CanvasEntity::new, MobCategory.MISC).sized(1.0F, 2.0F).build(Furniture.identifier("canvas").toString()));
    public static final RegistrySupplier<EntityType<PellsEntity>> PELLS = registerEntity("pells", () -> EntityType.Builder.of(PellsEntity::new, MobCategory.MISC).sized(1.0F, 2.0F).build(Furniture.identifier("pells").toString()));

    private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(final String path, final Supplier<T> type) {
        return BLOCK_ENTITY_TYPE_REGISTRAR.register(Furniture.identifier(path), type);
    }

    private static <T extends EntityType<?>> RegistrySupplier<T> registerEntity(final String path, final Supplier<T> type) {
        return ENTITY_TYPES.register(path, type);
    }

    private static Block[] toBlocks(Map<String, RegistrySupplier<Block>> blockSuppliers) {
        return blockSuppliers.values().stream().map(RegistrySupplier::get).toArray(Block[]::new);
    }

    public static void registerAttributes() {
        EntityAttributeRegistry.register(PELLS, PellsEntity::createMobAttributes);
    }

    static {
        BLOCK_ENTITY_TYPES.register();
        ENTITY_TYPES.register();
        registerAttributes();
    }
}