package net.satisfy.furniture.core.registry;

import net.satisfy.furniture.Furniture;
import net.satisfy.furniture.core.loot.RandomItemFunction;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

public class LootFunctionRegistry {
    private static final DeferredRegister<LootItemFunctionType<?>> LOOT_FUNCTIONS = DeferredRegister.create(Furniture.MOD_ID, Registries.LOOT_FUNCTION_TYPE);

    public static final RegistrySupplier<LootItemFunctionType<RandomItemFunction>> RANDOM_ITEM = LOOT_FUNCTIONS.register("random_item", () -> new LootItemFunctionType<>(RandomItemFunction.CODEC));

    static {
        LOOT_FUNCTIONS.register();
    }
}
