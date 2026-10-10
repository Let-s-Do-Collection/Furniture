package net.satisfy.furniture.fabric;

import net.satisfy.furniture.Furniture;
import net.satisfy.furniture.core.registry.FlammableBlockRegistry;
import net.satisfy.furniture.fabric.condition.CompatEnabledCondition;
import net.satisfy.furniture.fabric.config.FurnitureFabricConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class FurnitureFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AutoConfig.register(FurnitureFabricConfig.class, GsonConfigSerializer::new);
        CompatEnabledCondition.register();
        Furniture.init();
        FlammableBlockRegistry.registerFlammables();
    }
}
