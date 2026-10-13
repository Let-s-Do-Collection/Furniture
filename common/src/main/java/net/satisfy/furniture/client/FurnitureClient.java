package net.satisfy.furniture.client;

import net.satisfy.furniture.client.overlay.SteamVentInfoProvider;
import net.satisfy.furniture.client.overlay.BoatInAJarInfoProvider;
import net.satisfy.furniture.client.overlay.LampInfoProvider;
import net.satisfy.furniture.client.overlay.CashRegisterInfoProvider;
import net.satisfy.furniture.client.overlay.TelescopeInfoProvider;
import net.satisfy.furniture.client.handler.TelescopeViewHandler;
import net.satisfy.furniture.client.overlay.BinInfoProvider;
import net.satisfy.furniture.client.overlay.WardrobeInfoProvider;
import net.satisfy.furniture.client.overlay.FishTankInfoProvider;
import net.satisfy.furniture.client.overlay.PellsHud;
import net.satisfy.foundation.overlay.BlockInfoOverlay;
import net.satisfy.furniture.client.model.ClockModel;
import net.satisfy.furniture.client.model.FishTankModel;
import net.satisfy.furniture.client.model.GramophoneModel;
import net.satisfy.furniture.client.model.GrandfatherClockModel;
import net.satisfy.furniture.client.render.block.*;
import net.satisfy.furniture.client.render.entity.ChairRenderer;
import net.satisfy.furniture.client.render.entity.PellsRenderer;
import net.satisfy.furniture.core.registry.EntityTypeRegistry;
import net.satisfy.furniture.core.registry.TabRegistry;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.satisfy.foundation.client.creative.CreativeSideTabs;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

import static net.satisfy.furniture.core.registry.ObjectRegistry.*;

@Environment(EnvType.CLIENT)
public class FurnitureClient {

    public static void onInitializeClient() {
        RenderTypeRegistry.register(RenderType.cutout(), BIN.get(), GRAMOPHONE.get(), CASH_REGISTER.get(), BOAT_IN_A_JAR.get(), STREET_LANTERN.get(), STREET_WALL_LANTERN.get(), PLATED_STREET_LANTERN.get(), PLATED_STREET_WALL_LANTERN.get(), DISPLAY.get(), TERRARIUM.get());
        for (RegistrySupplier<Block> block : Stream.concat(Stream.concat(LAMPS.values().stream(), WALL_LAMPS.values().stream()), Stream.concat(BENCHES.values().stream(), Stream.concat(DESKS.values().stream(), Stream.concat(DESK_CHAIRS.values().stream(), WARDROBES.values().stream())))).toList()) {RenderTypeRegistry.register(RenderType.cutout(), block.get());
        }
        BlockEntityRendererRegistry.register(EntityTypeRegistry.GRANDFATHER_CLOCK_BLOCK_ENTITY.get(), GrandfatherClockRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.CLOCK_BLOCK_ENTITY.get(), ClockRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.GRAMOPHONE_BLOCK_ENTITY.get(), GramophoneRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.COFFER_BLOCK_ENTITY.get(), CofferRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.DISPLAY_BLOCK_ENTITY.get(), context -> new DisplayRenderer());
        BlockEntityRendererRegistry.register(EntityTypeRegistry.FISH_TANK_BLOCK_ENTITY.get(), FishTankRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.WARDROBE_BLOCK_ENTITY.get(), WardrobeRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.MIRROR_BLOCK_ENTITY.get(), MirrorRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.TELESCOPE_BLOCK_ENTITY.get(), context -> new TelescopeRenderer());
        BlockEntityRendererRegistry.register(EntityTypeRegistry.CASH_REGISTER_BLOCK_ENTITY.get(), context -> new CashRegisterRenderer());
        BlockEntityRendererRegistry.register(EntityTypeRegistry.BOAT_IN_A_JAR_BLOCK_ENTITY.get(), context -> new BoatInAJarRenderer());
        BlockEntityRendererRegistry.register(EntityTypeRegistry.STREET_LANTERN_BLOCK_ENTITY.get(), context -> new StreetLanternRenderer());

        BlockInfoOverlay.init();
        BlockInfoOverlay.registerProvider(new FishTankInfoProvider());
        BlockInfoOverlay.registerProvider(new WardrobeInfoProvider());
        BlockInfoOverlay.registerProvider(new BinInfoProvider());
        BlockInfoOverlay.registerProvider(new TelescopeInfoProvider());
        BlockInfoOverlay.registerProvider(new LampInfoProvider());
        BlockInfoOverlay.registerProvider(new CashRegisterInfoProvider());
        BlockInfoOverlay.registerProvider(new SteamVentInfoProvider());
        BlockInfoOverlay.registerProvider(new BoatInAJarInfoProvider());
        PellsHud.init();
        TelescopeViewHandler.init();

        if (TabRegistry.hasCompat()) {
            CreativeSideTabs.register(TabRegistry.FURNITURE_TAB.getKey(),
                    new CreativeSideTabs.SideTab(Component.translatable("creativetab.furniture.side.furniture"), () -> new ItemStack(BOAT_IN_A_JAR.get()), TabRegistry::acceptMain),
                    new CreativeSideTabs.SideTab(Component.translatable("creativetab.furniture.side.compat"), TabRegistry::compatIcon, TabRegistry::acceptCompat));
        }
    }

    public static void preInitClient() {
        registerEntityModelLayer();
        registerEntityRenderers();
    }

    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(EntityTypeRegistry.CHAIR, ChairRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.CANVAS, CanvasRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.PELLS, PellsRenderer::new);
    }

    public static void registerEntityModelLayer() {
        EntityModelLayerRegistry.register(GrandfatherClockModel.LAYER_LOCATION, GrandfatherClockModel::getTexturedModelData);
        EntityModelLayerRegistry.register(ClockModel.LAYER_LOCATION, ClockModel::getTexturedModelData);
        EntityModelLayerRegistry.register(GramophoneModel.LAYER_LOCATION, GramophoneModel::getTexturedModelData);
        EntityModelLayerRegistry.register(FishTankModel.LAYER_LOCATION, FishTankModel::getTexturedModelData);
        EntityModelLayerRegistry.register(CofferRenderer.LAYER_LOCATION, CofferRenderer::getTexturedModelData);
    }
}
