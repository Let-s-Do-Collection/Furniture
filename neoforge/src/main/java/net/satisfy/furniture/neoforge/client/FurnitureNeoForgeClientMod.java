package net.satisfy.furniture.neoforge.client;

import net.satisfy.furniture.Furniture;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Furniture.MOD_ID, dist = Dist.CLIENT)
public class FurnitureNeoForgeClientMod {
    public FurnitureNeoForgeClientMod(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
