package com.berksire.furniture.neoforge.client;

import com.berksire.furniture.Furniture;
import com.berksire.furniture.client.FurnitureClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = Furniture.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class FurnitureClientForge {

    @SubscribeEvent
    public static void onClientSetup(RegisterEvent event) {
        FurnitureClient.preInitClient();
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        FurnitureClient.onInitializeClient();
    }

}
