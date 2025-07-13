package com.berksire.furniture.neoforge;

import com.berksire.furniture.Furniture;
import com.berksire.furniture.registry.FlammableBlockRegistry;
import dev.architectury.platform.hooks.EventBusesHooks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(Furniture.MODID)
public class FurnitureForge {
    public FurnitureForge(IEventBus modEventBus, ModContainer modContainer) {
        EventBusesHooks.whenAvailable(Furniture.MODID, IEventBus::start);
        Furniture.init();

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(FlammableBlockRegistry::registerFlammables);
        Furniture.commonSetup();
    }
}
