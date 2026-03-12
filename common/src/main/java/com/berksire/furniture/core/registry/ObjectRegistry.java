package com.berksire.furniture.core.registry;

import com.berksire.furniture.Furniture;
import com.berksire.furniture.core.block.BenchBlock;
import com.berksire.furniture.core.block.BinBlock;
import com.berksire.furniture.core.block.BoatInAJarBlock;
import com.berksire.furniture.core.block.BlueprintsBlock;
import com.berksire.furniture.core.block.CabinetBlock;
import com.berksire.furniture.core.block.CashRegisterBlock;
import com.berksire.furniture.core.block.ChimneyBlock;
import com.berksire.furniture.core.block.ClockBlock;
import com.berksire.furniture.core.block.CofferBlock;
import com.berksire.furniture.core.block.CopperChimneyBlock;
import com.berksire.furniture.core.block.CurtainBlock;
import com.berksire.furniture.core.block.DeskBlock;
import com.berksire.furniture.core.block.DeskChairBlock;
import com.berksire.furniture.core.block.DisplayBlock;
import com.berksire.furniture.core.block.DresserBlock;
import com.berksire.furniture.core.block.ExplorersBoxBlock;
import com.berksire.furniture.core.block.FishTankBlock;
import com.berksire.furniture.core.block.GramophoneBlock;
import com.berksire.furniture.core.block.GrandfatherClockBlock;
import com.berksire.furniture.core.block.LampBlock;
import com.berksire.furniture.core.block.LampWallBlock;
import com.berksire.furniture.core.block.MirrorBlock;
import com.berksire.furniture.core.block.PlanterBlock;
import com.berksire.furniture.core.block.PouffeBlock;
import com.berksire.furniture.core.block.SewingKitBlock;
import com.berksire.furniture.core.block.ShutterBlock;
import com.berksire.furniture.core.block.SofaBlock;
import com.berksire.furniture.core.block.SteamVentBlock;
import com.berksire.furniture.core.block.StreetLanternBlock;
import com.berksire.furniture.core.block.StreetLanternWallBlock;
import com.berksire.furniture.core.block.TelescopeBlock;
import com.berksire.furniture.core.block.TerrariumBlock;
import com.berksire.furniture.core.block.ToolBoxBlock;
import com.berksire.furniture.core.block.WardrobeBlock;
import com.berksire.furniture.core.item.CanvasItem;
import com.berksire.furniture.core.item.PellsSpawnItem;
import com.berksire.furniture.core.item.TrashBagItem;
import com.berksire.furniture.core.util.GeneralUtil;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ObjectRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Furniture.MOD_ID, Registries.ITEM);
    public static final Registrar<Item> ITEM_REGISTRAR = ITEMS.getRegistrar();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Furniture.MOD_ID, Registries.BLOCK);
    public static final Registrar<Block> BLOCK_REGISTRAR = BLOCKS.getRegistrar();

    public static final Map<String, RegistrySupplier<Block>> SOFAS = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> POUFFE = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> LAMPS = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> WALL_LAMPS = new HashMap<>();
    public static final Map<String, RegistrySupplier<Item>> LAMP_ITEMS = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> CURTAINS = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> CABINETS = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> DESK_CHAIRS = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> DESKS = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> DRESSER = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> GRANDFATHER_CLOCKS = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> CLOCKS = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> BENCHES = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> MIRRORS = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> SHUTTERS = new HashMap<>();
    public static final Map<String, RegistrySupplier<Block>> WARDROBES = new HashMap<>();

    public static final RegistrySupplier<Block> GRAMOPHONE = registerWithItem("gramophone", () -> new GramophoneBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.JUKEBOX)));
    public static final RegistrySupplier<Block> TELESCOPE = registerWithItem("telescope", () -> new TelescopeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));
    public static final RegistrySupplier<Block> COFFER = registerWithItem("coffer", () -> new CofferBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_WOOL).pushReaction(PushReaction.DESTROY)));
    public static final RegistrySupplier<Block> EXPLORERS_BOX = registerWithItem("explorers_box", () -> new ExplorersBoxBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CARTOGRAPHY_TABLE)));
    public static final RegistrySupplier<Block> CASH_REGISTER = registerWithItem("cash_register", () -> new CashRegisterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final RegistrySupplier<Block> TOOL_BOX = registerWithItem("tool_box", () -> new ToolBoxBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SMITHING_TABLE).pushReaction(PushReaction.DESTROY)));
    public static final RegistrySupplier<Block> BLUEPRINTS = registerWithItem("blueprints", () -> new BlueprintsBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAKE).instabreak()));
    public static final RegistrySupplier<Block> SEWING_KIT = registerWithItem("sewing_kit", () -> new SewingKitBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LOOM)));
    public static final RegistrySupplier<Item> CANVAS = registerItem("canvas", () -> new CanvasItem(new Item.Properties(), CanvasRegistry.LONELY_DAISY, TagRegistry.PAINTINGS));
    public static final RegistrySupplier<Block> BIN = registerWithItem("bin", () -> new BinBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<Item> TRASH_BAG = registerItem("trash_bag", () -> new TrashBagItem(new Item.Properties()));
    public static final RegistrySupplier<Block> STEAM_VENT = registerWithItem("steam_vent", () -> new SteamVentBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<Block> COPPER_FISH_TANK = registerWithItem("copper_fish_tank", () -> new FishTankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final RegistrySupplier<Block> IRON_FISH_TANK = registerWithItem("iron_fish_tank", () -> new FishTankBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<Block> BRICK_CHIMNEY = registerWithItem("brick_chimney", () -> new ChimneyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<Block> STONE_BRICKS_CHIMNEY = registerWithItem("stone_bricks_chimney", () -> new ChimneyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));
    public static final RegistrySupplier<Block> COPPER_CHIMNEY = registerWithItem("copper_chimney", () -> new CopperChimneyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));
    public static final RegistrySupplier<Block> BOAT_IN_A_JAR = registerWithItem("boat_in_a_jar", () -> new BoatInAJarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final RegistrySupplier<Block> STREET_LANTERN = registerWithoutItem("street_lantern", () -> new StreetLanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DECORATED_POT).lightLevel(StreetLanternBlock::vanillaLightLevel)));
    public static final RegistrySupplier<Block> STREET_WALL_LANTERN = registerWithoutItem("street_lantern_wall", () -> new StreetLanternWallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DECORATED_POT).lightLevel(StreetLanternBlock::vanillaLightLevel)));
    public static final RegistrySupplier<Item> STREET_LANTERN_ITEM = registerItem("street_lantern_item", () -> new StandingAndWallBlockItem(ObjectRegistry.STREET_LANTERN.get(), ObjectRegistry.STREET_WALL_LANTERN.get(), new Item.Properties(), Direction.DOWN));
    public static final RegistrySupplier<Block> PLATED_STREET_LANTERN = registerWithoutItem("plated_street_lantern", () -> new StreetLanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DECORATED_POT).lightLevel(StreetLanternBlock::vanillaLightLevel)));
    public static final RegistrySupplier<Block> PLATED_STREET_WALL_LANTERN = registerWithoutItem("plated_street_lantern_wall", () -> new StreetLanternWallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DECORATED_POT).lightLevel(StreetLanternBlock::vanillaLightLevel)));
    public static final RegistrySupplier<Item> PLATED_STREET_LANTERN_ITEM = registerItem("plated_street_lantern_item", () -> new StandingAndWallBlockItem(ObjectRegistry.PLATED_STREET_LANTERN.get(), ObjectRegistry.PLATED_STREET_WALL_LANTERN.get(), new Item.Properties(), Direction.DOWN));
    public static final RegistrySupplier<Item> PELLS = registerItem("pells", () -> new PellsSpawnItem(new Item.Properties()));
    public static final RegistrySupplier<Item> CPHS_PRIDE = registerItem("cphs_pride", () -> new Item(new Item.Properties().stacksTo(1).jukeboxPlayable(JukeboxSongRegistry.CPHS_PRIDE)));
    public static final RegistrySupplier<Item> LETSDO_THEME = registerItem("letsdo_theme", () -> new Item(new Item.Properties().stacksTo(1).jukeboxPlayable(JukeboxSongRegistry.LETSDO_THEME)));
    public static final RegistrySupplier<Block> DISPLAY = registerWithItem("display", () -> new DisplayBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final RegistrySupplier<Block> TERRARIUM = registerWithItem("terrarium", () -> new TerrariumBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)));
    public static final RegistrySupplier<Block> WOODEN_PLANTER = registerWithItem("wooden_planter", () -> new PlanterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)));
    public static final RegistrySupplier<Block> STONE_BRICK_PLANTER = registerWithItem("stone_brick_planter", () -> new PlanterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

    public static final String[] colors = {
            "white", "light_gray", "gray", "black", "red", "orange", "yellow", "lime", "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink", "brown"
    };

    public static final String[] meadowTextileTypes = {
            "rustic", "linen", "jacquard", "plaid", "chambray", "tweed", "warped"
    };

    public static final String[] alpineWhispersTextileTypes = {
            "homespun"
    };

    public static final String[] vanillaWoodTypes = {
            "oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry"
    };

    public static final String[] bloomingNatureWoodTypes = {
            "aspen", "larch", "baobab", "cypress", "ebony", "chestnut", "fan_palm", "fir", "swamp_oak", "swamp_cypress"
    };

    public static final String[] meadowWoodTypes = {
            "pine"
    };

    public static final String[] beachpartyWoodTypes = {
            "palm"
    };

    public static final String[] alpineWhispersWoodTypes = {
            "arolla_pine"
    };

    public static final String[] vineryWoodTypes = {
            "dark_cherry"
    };

    public static final String[] woodTypes;

    static {
        String[] resolvedWoodTypes = vanillaWoodTypes;

        if (Platform.isModLoaded("bloomingnature")) {
            resolvedWoodTypes = concat(resolvedWoodTypes, bloomingNatureWoodTypes);
        }

        if (Platform.isModLoaded("meadow")) {
            resolvedWoodTypes = concat(resolvedWoodTypes, meadowWoodTypes);
        }

        if (Platform.isModLoaded("beachparty")) {
            resolvedWoodTypes = concat(resolvedWoodTypes, beachpartyWoodTypes);
        }

        if (Platform.isModLoaded("alpinewhispers")) {
            resolvedWoodTypes = concat(resolvedWoodTypes, alpineWhispersWoodTypes);
        }

        if (Platform.isModLoaded("vinery")) {
            resolvedWoodTypes = concat(resolvedWoodTypes, vineryWoodTypes);
        }

        woodTypes = resolvedWoodTypes;

        for (String woodType : woodTypes) {
            Block plankBlock = getCorrespondingPlank(woodType);

            BENCHES.put(woodType, registerWithItem(woodType + "_bench", () -> new BenchBlock(BlockBehaviour.Properties.ofFullCopy(plankBlock).pushReaction(PushReaction.IGNORE))));
            CABINETS.put(woodType, registerWithItem(woodType + "_cabinet", () -> new CabinetBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), SoundRegistry.CABINET_OPEN, SoundRegistry.CABINET_CLOSE)));

            ClockBlock.WoodType clockWoodType = isVanillaWoodType(woodType) ? ClockBlock.WoodType.valueOf(woodType.toUpperCase(Locale.ROOT)) : ClockBlock.WoodType.OAK;

            CLOCKS.put(woodType, registerWithItem(woodType + "_clock", () -> new ClockBlock(BlockBehaviour.Properties.ofFullCopy(plankBlock).pushReaction(PushReaction.IGNORE), clockWoodType)));
            GRANDFATHER_CLOCKS.put(woodType, registerWithItem(woodType + "_grandfather_clock", () -> new GrandfatherClockBlock(BlockBehaviour.Properties.ofFullCopy(plankBlock).pushReaction(PushReaction.IGNORE))));

            MIRRORS.put(woodType, registerWithItem(woodType + "_mirror", () -> new MirrorBlock(BlockBehaviour.Properties.ofFullCopy(plankBlock).pushReaction(PushReaction.IGNORE))));
            DESK_CHAIRS.put(woodType, registerWithItem(woodType + "_desk_chair", () -> new DeskChairBlock(BlockBehaviour.Properties.ofFullCopy(plankBlock))));

            if (!isMeadowWoodType(woodType)) {
                SHUTTERS.put(woodType, registerWithItem(woodType + "_shutter", () -> new ShutterBlock(BlockBehaviour.Properties.ofFullCopy(plankBlock).pushReaction(PushReaction.IGNORE))));
            }

            if (!isMeadowWoodType(woodType) && !isAlpineWhispersWoodType(woodType)) {
                DESKS.put(woodType, registerWithItem(woodType + "_desk", () -> new DeskBlock(BlockBehaviour.Properties.ofFullCopy(plankBlock).pushReaction(PushReaction.IGNORE))));
                DRESSER.put(woodType, registerWithItem(woodType + "_dresser", () -> new DresserBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD), SoundRegistry.CABINET_OPEN, SoundRegistry.CABINET_CLOSE)));
                WARDROBES.put(woodType, registerWithItem(woodType + "_wardrobe", () -> new WardrobeBlock(BlockBehaviour.Properties.of().strength(2.0F, 3.0F).sound(SoundType.WOOD))));
            }
        }

        for (String color : colors) {
            DyeColor dyeColor = DyeColor.valueOf(color.toUpperCase(Locale.ROOT));

            SOFAS.put(color, registerWithItem("sofa_" + color, () -> new SofaBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).pushReaction(PushReaction.DESTROY), dyeColor)));
            POUFFE.put(color, registerWithItem("pouffe_" + color, () -> new PouffeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_WOOL).pushReaction(PushReaction.NORMAL), dyeColor)));
            CURTAINS.put(color, registerWithItem("curtain_" + color, () -> new CurtainBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_WOOL).pushReaction(PushReaction.DESTROY), dyeColor)));

            String lampName = "lamp_" + color;
            String wallLampName = "lamp_wall_" + color;

            RegistrySupplier<Block> lamp = registerWithoutItem(lampName, () -> new LampBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).lightLevel(state -> state.getValue(AbstractCandleBlock.LIT) ? 15 : 0).pushReaction(PushReaction.DESTROY), dyeColor));
            LAMPS.put(color, lamp);

            RegistrySupplier<Block> wallLamp = registerWithoutItem(wallLampName, () -> new LampWallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).lightLevel(state -> state.getValue(AbstractCandleBlock.LIT) ? 15 : 0).pushReaction(PushReaction.DESTROY), dyeColor));
            WALL_LAMPS.put(color, wallLamp);

            LAMP_ITEMS.put(color, registerItem(lampName, () -> new StandingAndWallBlockItem(lamp.get(), wallLamp.get(), new Item.Properties(), Direction.DOWN)));
        }

        if (Platform.isModLoaded("meadow")) {
            Block meadowLampPlank = getModdedPlank("meadow", "pine");

            for (String meadowTextileType : meadowTextileTypes) {
                String meadowKey = "meadow_" + meadowTextileType;

                CURTAINS.put(meadowKey, registerWithItem("curtain_" + meadowTextileType, () -> {
                    Block meadowWoolBlock = getOptionalBlock("meadow", meadowTextileType + "_wool");
                    BlockBehaviour.Properties curtainProperties = meadowWoolBlock != Blocks.AIR ? BlockBehaviour.Properties.ofFullCopy(meadowWoolBlock) : BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL);
                    return new CurtainBlock(curtainProperties.pushReaction(PushReaction.DESTROY), DyeColor.WHITE);
                }));

                POUFFE.put(meadowKey, registerWithItem("pouffe_" + meadowTextileType, () -> {
                    Block meadowWoolBlock = getOptionalBlock("meadow", meadowTextileType + "_wool");
                    BlockBehaviour.Properties pouffeProperties = meadowWoolBlock != Blocks.AIR ? BlockBehaviour.Properties.ofFullCopy(meadowWoolBlock) : BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL);
                    return new PouffeBlock(pouffeProperties.pushReaction(PushReaction.NORMAL), DyeColor.WHITE);
                }));

                String lampName = "lamp_" + meadowTextileType;
                String wallLampName = "lamp_wall_" + meadowTextileType;

                RegistrySupplier<Block> lamp = registerWithoutItem(lampName, () -> new LampBlock(BlockBehaviour.Properties.ofFullCopy(meadowLampPlank).lightLevel(state -> state.getValue(AbstractCandleBlock.LIT) ? 15 : 0).pushReaction(PushReaction.DESTROY), DyeColor.WHITE));
                LAMPS.put(meadowKey, lamp);

                RegistrySupplier<Block> wallLamp = registerWithoutItem(wallLampName, () -> new LampWallBlock(BlockBehaviour.Properties.ofFullCopy(meadowLampPlank).lightLevel(state -> state.getValue(AbstractCandleBlock.LIT) ? 15 : 0).pushReaction(PushReaction.DESTROY), DyeColor.WHITE));
                WALL_LAMPS.put(meadowKey, wallLamp);

                LAMP_ITEMS.put(meadowKey, registerItem(lampName, () -> new StandingAndWallBlockItem(lamp.get(), wallLamp.get(), new Item.Properties(), Direction.DOWN)));
            }
        }

        if (Platform.isModLoaded("alpinewhispers")) {
            Block alpineLampPlank = getModdedPlank("alpinewhispers", "arolla_pine");

            for (String alpineWhispersTextileType : alpineWhispersTextileTypes) {
                String alpineWhispersKey = "alpinewhispers_" + alpineWhispersTextileType;

                CURTAINS.put(alpineWhispersKey, registerWithItem("curtain_" + alpineWhispersTextileType, () -> {
                    Block alpineWhispersWoolBlock = getOptionalBlock("alpinewhispers", alpineWhispersTextileType + "_wool");
                    BlockBehaviour.Properties curtainProperties = alpineWhispersWoolBlock != Blocks.AIR ? BlockBehaviour.Properties.ofFullCopy(alpineWhispersWoolBlock) : BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL);
                    return new CurtainBlock(curtainProperties.pushReaction(PushReaction.DESTROY), DyeColor.WHITE);
                }));

                POUFFE.put(alpineWhispersKey, registerWithItem("pouffe_" + alpineWhispersTextileType, () -> {
                    Block alpineWhispersWoolBlock = getOptionalBlock("alpinewhispers", alpineWhispersTextileType + "_wool");
                    BlockBehaviour.Properties pouffeProperties = alpineWhispersWoolBlock != Blocks.AIR ? BlockBehaviour.Properties.ofFullCopy(alpineWhispersWoolBlock) : BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_WOOL);
                    return new PouffeBlock(pouffeProperties.pushReaction(PushReaction.NORMAL), DyeColor.WHITE);
                }));

                String lampName = "lamp_" + alpineWhispersTextileType;
                String wallLampName = "lamp_wall_" + alpineWhispersTextileType;

                RegistrySupplier<Block> lamp = registerWithoutItem(lampName, () -> new LampBlock(BlockBehaviour.Properties.ofFullCopy(alpineLampPlank).lightLevel(state -> state.getValue(AbstractCandleBlock.LIT) ? 15 : 0).pushReaction(PushReaction.DESTROY), DyeColor.WHITE));
                LAMPS.put(alpineWhispersKey, lamp);

                RegistrySupplier<Block> wallLamp = registerWithoutItem(wallLampName, () -> new LampWallBlock(BlockBehaviour.Properties.ofFullCopy(alpineLampPlank).lightLevel(state -> state.getValue(AbstractCandleBlock.LIT) ? 15 : 0).pushReaction(PushReaction.DESTROY), DyeColor.WHITE));
                WALL_LAMPS.put(alpineWhispersKey, wallLamp);

                LAMP_ITEMS.put(alpineWhispersKey, registerItem(lampName, () -> new StandingAndWallBlockItem(lamp.get(), wallLamp.get(), new Item.Properties(), Direction.DOWN)));
            }
        }

        BLOCKS.register();
        ITEMS.register();
    }

    private static boolean isVanillaWoodType(String woodType) {
        for (String vanillaWoodType : vanillaWoodTypes) {
            if (vanillaWoodType.equals(woodType)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isMeadowWoodType(String woodType) {
        for (String meadowWoodType : meadowWoodTypes) {
            if (meadowWoodType.equals(woodType)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAlpineWhispersWoodType(String woodType) {
        for (String alpineWhispersWoodType : alpineWhispersWoodTypes) {
            if (alpineWhispersWoodType.equals(woodType)) {
                return true;
            }
        }
        return false;
    }

    private static String[] concat(String[] first, String[] second) {
        String[] result = new String[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private static Block getCorrespondingPlank(String woodType) {
        return switch (woodType) {
            case "spruce" -> Blocks.SPRUCE_PLANKS;
            case "birch" -> Blocks.BIRCH_PLANKS;
            case "jungle" -> Blocks.JUNGLE_PLANKS;
            case "acacia" -> Blocks.ACACIA_PLANKS;
            case "dark_oak" -> Blocks.DARK_OAK_PLANKS;
            case "mangrove" -> Blocks.MANGROVE_PLANKS;
            case "cherry" -> Blocks.CHERRY_PLANKS;
            case "oak" -> Blocks.OAK_PLANKS;
            default -> getModdedPlank(getWoodNamespace(woodType), woodType);
        };
    }

    private static String getWoodNamespace(String woodType) {
        if (isMeadowWoodType(woodType)) {
            return "meadow";
        }
        if (isAlpineWhispersWoodType(woodType)) {
            return "alpinewhispers";
        }
        for (String beachpartyWoodType : beachpartyWoodTypes) {
            if (beachpartyWoodType.equals(woodType)) {
                return "beachparty";
            }
        }
        for (String vineryWoodType : vineryWoodTypes) {
            if (vineryWoodType.equals(woodType)) {
                return "vinery";
            }
        }
        return "bloomingnature";
    }

    private static Block getModdedPlank(String namespace, String woodType) {
        ResourceLocation plankId = ResourceLocation.fromNamespaceAndPath(namespace, woodType + "_planks");
        Block plankBlock = BuiltInRegistries.BLOCK.get(plankId);
        if (plankBlock != Blocks.AIR) {
            return plankBlock;
        }
        return Blocks.OAK_PLANKS;
    }

    private static Block getOptionalBlock(String namespace, String path) {
        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(namespace, path);
        Block resolvedBlock = BuiltInRegistries.BLOCK.get(resourceLocation);
        if (resolvedBlock != Blocks.AIR) {
            return resolvedBlock;
        }
        return Blocks.AIR;
    }

    private static Item.Properties getSettings(Consumer<Item.Properties> consumer) {
        Item.Properties settings = new Item.Properties();
        consumer.accept(settings);
        return settings;
    }

    static Item.Properties getSettings() {
        return getSettings(settings -> {
        });
    }

    public static <T extends Block> RegistrySupplier<T> registerWithItem(String name, Supplier<T> block) {
        return GeneralUtil.registerWithItem(BLOCKS, BLOCK_REGISTRAR, ITEMS, ITEM_REGISTRAR, Furniture.identifier(name), block);
    }

    public static <T extends Block> RegistrySupplier<T> registerWithoutItem(String path, Supplier<T> block) {
        return GeneralUtil.registerWithoutItem(BLOCKS, BLOCK_REGISTRAR, Furniture.identifier(path), block);
    }

    public static <T extends Item> RegistrySupplier<T> registerItem(String path, Supplier<T> itemSupplier) {
        return GeneralUtil.registerItem(ITEMS, ITEM_REGISTRAR, Furniture.identifier(path), itemSupplier);
    }
}