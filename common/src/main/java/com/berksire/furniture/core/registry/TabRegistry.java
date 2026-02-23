package com.berksire.furniture.core.registry;

import com.berksire.furniture.Furniture;
import dev.architectury.platform.Platform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Map;

@SuppressWarnings("unused")
public class TabRegistry {
    public static final DeferredRegister<CreativeModeTab> FURNITURE_TABS = DeferredRegister.create(Furniture.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> FURNITURE_TAB = FURNITURE_TABS.register("furniture", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(ObjectRegistry.BOAT_IN_A_JAR.get()))
            .title(Component.translatable("itemGroup.furniture.furniture_tab"))
            .displayItems((parameters, out) -> {
                String[] colorOrder = {
                        "white", "light_gray", "gray", "black", "red", "orange", "yellow", "lime", "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink", "brown"
                };
                String[] woodTypeOrder = {
                        "oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry"
                };

                for (String color : colorOrder) acceptIfPresent(ObjectRegistry.SOFAS, color, out);
                for (String color : colorOrder) acceptIfPresent(ObjectRegistry.POUFFE, color, out);
                for (String color : colorOrder) acceptIfPresent(ObjectRegistry.LAMP_ITEMS, color, out);

                for (String woodType : woodTypeOrder) acceptIfPresent(ObjectRegistry.SHUTTERS, woodType, out);
                for (String woodType : woodTypeOrder) acceptIfPresent(ObjectRegistry.BENCHES, woodType, out);
                for (String woodType : woodTypeOrder) acceptIfPresent(ObjectRegistry.DESK_CHAIRS, woodType, out);
                for (String woodType : woodTypeOrder) acceptIfPresent(ObjectRegistry.CABINETS, woodType, out);
                for (String woodType : woodTypeOrder) acceptIfPresent(ObjectRegistry.DRESSER, woodType, out);
                for (String woodType : woodTypeOrder) acceptIfPresent(ObjectRegistry.WARDROBES, woodType, out);
                for (String woodType : woodTypeOrder) acceptIfPresent(ObjectRegistry.DESKS, woodType, out);

                out.accept(ObjectRegistry.WOODEN_PLANTER.get());
                out.accept(ObjectRegistry.STONE_BRICK_PLANTER.get());
                out.accept(ObjectRegistry.STEAM_VENT.get());
                out.accept(ObjectRegistry.STONE_BRICKS_CHIMNEY.get());
                out.accept(ObjectRegistry.BRICK_CHIMNEY.get());
                out.accept(ObjectRegistry.COPPER_CHIMNEY.get());

                for (String woodType : woodTypeOrder) acceptIfPresent(ObjectRegistry.CLOCKS, woodType, out);
                for (String woodType : woodTypeOrder) acceptIfPresent(ObjectRegistry.GRANDFATHER_CLOCKS, woodType, out);
                for (String woodType : woodTypeOrder) acceptIfPresent(ObjectRegistry.MIRRORS, woodType, out);
                for (String color : colorOrder) acceptIfPresent(ObjectRegistry.CURTAINS, color, out);

                out.accept(ObjectRegistry.TELESCOPE.get());
                out.accept(ObjectRegistry.GRAMOPHONE.get());
                out.accept(ObjectRegistry.CASH_REGISTER.get());
                out.accept(ObjectRegistry.COFFER.get());
                out.accept(ObjectRegistry.TOOL_BOX.get());
                out.accept(ObjectRegistry.SEWING_KIT.get());
                out.accept(ObjectRegistry.EXPLORERS_BOX.get());
                out.accept(ObjectRegistry.BLUEPRINTS.get());
                out.accept(ObjectRegistry.STREET_LANTERN_ITEM.get());
                out.accept(ObjectRegistry.PLATED_STREET_LANTERN_ITEM.get());
                out.accept(ObjectRegistry.PELLS.get());
                out.accept(ObjectRegistry.BOAT_IN_A_JAR.get());
                out.accept(ObjectRegistry.COPPER_FISH_TANK.get());
                out.accept(ObjectRegistry.IRON_FISH_TANK.get());
                out.accept(ObjectRegistry.TERRARIUM.get());
                out.accept(ObjectRegistry.DISPLAY.get());
                out.accept(ObjectRegistry.CANVAS.get());
                out.accept(ObjectRegistry.CPHS_PRIDE.get());
                out.accept(ObjectRegistry.LETSDO_THEME.get());
                out.accept(ObjectRegistry.BIN.get());
                out.accept(ObjectRegistry.TRASH_BAG.get());
            })
            .build());

    public static RegistrySupplier<CreativeModeTab> FURNITURE_COMPAT_LAYER_TAB;

    static {
        boolean bloomingNatureLoaded = Platform.isModLoaded("bloomingnature");
        boolean meadowLoaded = Platform.isModLoaded("meadow");

        if (bloomingNatureLoaded || meadowLoaded) {
            FURNITURE_COMPAT_LAYER_TAB = FURNITURE_TABS.register("furniture_compat_layer", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 1)
                    .icon(() -> buildCompatIcon(bloomingNatureLoaded, meadowLoaded))
                    .title(Component.translatable("itemGroup.furniture.furniture_compat_layer_tab"))
                    .displayItems((parameters, out) -> {
                        String[] bloomingNatureWoodTypeOrder = {
                                "aspen", "larch", "baobab", "cypress", "ebony", "chestnut", "fan_palm", "fir", "swamp_oak", "swamp_cypress"
                        };
                        String[] meadowWoodTypeOrder = {
                                "pine"
                        };
                        String[] compatWoodTypeOrder = buildCompatWoodTypeOrder(bloomingNatureLoaded, meadowLoaded, bloomingNatureWoodTypeOrder, meadowWoodTypeOrder);

                        for (String woodType : compatWoodTypeOrder) acceptIfPresent(ObjectRegistry.SHUTTERS, woodType, out);
                        for (String woodType : compatWoodTypeOrder) acceptIfPresent(ObjectRegistry.BENCHES, woodType, out);
                        for (String woodType : compatWoodTypeOrder) acceptIfPresent(ObjectRegistry.DESK_CHAIRS, woodType, out);
                        for (String woodType : compatWoodTypeOrder) acceptIfPresent(ObjectRegistry.CABINETS, woodType, out);
                        for (String woodType : compatWoodTypeOrder) acceptIfPresent(ObjectRegistry.DRESSER, woodType, out);
                        for (String woodType : compatWoodTypeOrder) acceptIfPresent(ObjectRegistry.WARDROBES, woodType, out);
                        for (String woodType : compatWoodTypeOrder) acceptIfPresent(ObjectRegistry.DESKS, woodType, out);
                        for (String woodType : compatWoodTypeOrder) acceptIfPresent(ObjectRegistry.CLOCKS, woodType, out);
                        for (String woodType : compatWoodTypeOrder) acceptIfPresent(ObjectRegistry.GRANDFATHER_CLOCKS, woodType, out);
                        for (String woodType : compatWoodTypeOrder) acceptIfPresent(ObjectRegistry.MIRRORS, woodType, out);

                        if (meadowLoaded) {
                            String[] meadowTextileOrder = {
                                    "rustic", "linen", "jacquard", "plaid", "chambray", "tweed", "warped"
                            };
                            for (String textile : meadowTextileOrder) acceptIfPresent(ObjectRegistry.POUFFE, "meadow_" + textile, out);
                            for (String textile : meadowTextileOrder) acceptIfPresent(ObjectRegistry.CURTAINS, "meadow_" + textile, out);
                            for (String textile : meadowTextileOrder) acceptIfPresent(ObjectRegistry.LAMP_ITEMS, "meadow_" + textile, out);
                        }
                    })
                    .build());
        }

        FURNITURE_TABS.register();
    }

    private static String[] buildCompatWoodTypeOrder(boolean bloomingNatureLoaded, boolean meadowLoaded, String[] bloomingNatureWoodTypeOrder, String[] meadowWoodTypeOrder) {
        if (bloomingNatureLoaded && meadowLoaded) {
            return concat(bloomingNatureWoodTypeOrder, meadowWoodTypeOrder);
        }
        if (bloomingNatureLoaded) {
            return bloomingNatureWoodTypeOrder;
        }
        if (meadowLoaded) {
            return meadowWoodTypeOrder;
        }
        return new String[0];
    }

    private static String[] concat(String[] first, String[] second) {
        String[] result = new String[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private static ItemStack buildCompatIcon(boolean bloomingNatureLoaded, boolean meadowLoaded) {
        if (bloomingNatureLoaded) {
            RegistrySupplier<?> supplier = ObjectRegistry.CABINETS.get("aspen");
            if (supplier != null) {
                Object value = supplier.get();
                if (value instanceof ItemLike itemLike) return new ItemStack(itemLike);
            }
        }
        if (meadowLoaded) {
            RegistrySupplier<?> supplier = ObjectRegistry.CABINETS.get("pine");
            if (supplier != null) {
                Object value = supplier.get();
                if (value instanceof ItemLike itemLike) return new ItemStack(itemLike);
            }
        }
        return new ItemStack(ObjectRegistry.BOAT_IN_A_JAR.get());
    }

    private static void acceptIfPresent(Map<String, ? extends RegistrySupplier<?>> registrySuppliers, String key, CreativeModeTab.Output out) {
        RegistrySupplier<?> supplier = registrySuppliers.get(key);
        if (supplier != null) {
            supplier.ifPresent(value -> {
                if (value instanceof ItemLike itemLike) {
                    out.accept(new ItemStack(itemLike));
                }
            });
        }
    }
}