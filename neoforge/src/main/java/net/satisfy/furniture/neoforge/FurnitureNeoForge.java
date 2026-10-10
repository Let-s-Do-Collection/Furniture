package net.satisfy.furniture.neoforge;

import net.satisfy.furniture.Furniture;
import net.satisfy.furniture.core.registry.FlammableBlockRegistry;
import net.satisfy.furniture.neoforge.condition.CompatEnabledCondition;
import net.satisfy.furniture.neoforge.config.FurnitureNeoForgeConfig;
import dev.architectury.platform.hooks.EventBusesHooks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Furniture.MOD_ID)
public class FurnitureNeoForge {
    public FurnitureNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        EventBusesHooks.whenAvailable(Furniture.MOD_ID, IEventBus::start);
        modContainer.registerConfig(ModConfig.Type.STARTUP, FurnitureNeoForgeConfig.STARTUP_CONFIG);
        modContainer.registerConfig(ModConfig.Type.COMMON, FurnitureNeoForgeConfig.COMMON_CONFIG);
        modEventBus.addListener((ModConfigEvent.Loading event) -> {
            if (event.getConfig().getSpec() == FurnitureNeoForgeConfig.COMMON_CONFIG) FurnitureNeoForgeConfig.sync();
        });
        modEventBus.addListener((ModConfigEvent.Reloading event) -> {
            if (event.getConfig().getSpec() == FurnitureNeoForgeConfig.COMMON_CONFIG) FurnitureNeoForgeConfig.sync();
        });
        CompatEnabledCondition.register(modEventBus);
        Furniture.init();

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(FlammableBlockRegistry::registerFlammables);
        Furniture.commonSetup();
    }
}
