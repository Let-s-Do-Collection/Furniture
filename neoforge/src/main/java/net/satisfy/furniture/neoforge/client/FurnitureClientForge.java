package net.satisfy.furniture.neoforge.client;

import net.satisfy.furniture.Furniture;
import net.satisfy.furniture.client.FurnitureClient;
import net.satisfy.furniture.client.render.BakedPartModels;
import net.satisfy.furniture.client.particle.GramophoneNoteParticle;
import net.satisfy.furniture.core.registry.ParticleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

@EventBusSubscriber(modid = Furniture.MOD_ID, value = Dist.CLIENT)
public class FurnitureClientForge {

    @SubscribeEvent
    public static void onClientSetup(RegisterEvent event) {
        FurnitureClient.preInitClient();
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        FurnitureClient.onInitializeClient();
    }

    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ParticleRegistry.GRAMOPHONE_NOTE.get(), GramophoneNoteParticle.Provider::new);
    }

    @SubscribeEvent
    public static void onRegisterModels(ModelEvent.RegisterAdditional event) {
        BakedPartModels.ALL.forEach(id -> event.register(ModelResourceLocation.standalone(id)));
        BakedPartModels.lookup = id -> Minecraft.getInstance().getModelManager().getModel(ModelResourceLocation.standalone(id));
    }
}
