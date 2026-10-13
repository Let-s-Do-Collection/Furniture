package net.satisfy.furniture.fabric.client;

import net.satisfy.furniture.client.FurnitureClient;
import net.satisfy.furniture.client.render.BakedPartModels;
import net.satisfy.furniture.client.particle.GramophoneNoteParticle;
import net.satisfy.furniture.core.registry.ParticleRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.model.loading.v1.FabricBakedModelManager;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.Minecraft;

public class FurnitureClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(context -> context.addModels(BakedPartModels.ALL));
        BakedPartModels.lookup = id -> ((FabricBakedModelManager) Minecraft.getInstance().getModelManager()).getModel(id);
        ParticleFactoryRegistry.getInstance().register(ParticleRegistry.GRAMOPHONE_NOTE.get(), GramophoneNoteParticle.Provider::new);
        FurnitureClient.preInitClient();
        FurnitureClient.onInitializeClient();
    }
}
