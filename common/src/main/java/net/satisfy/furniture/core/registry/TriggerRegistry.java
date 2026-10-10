package net.satisfy.furniture.core.registry;

import net.satisfy.furniture.Furniture;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.satisfy.foundation.advancement.SimplePlayerTrigger;

public class TriggerRegistry {
    private static final DeferredRegister<CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Furniture.MOD_ID, Registries.TRIGGER_TYPE);

    public static final RegistrySupplier<SimplePlayerTrigger> SHOOTING_STAR = TRIGGERS.register("shooting_star", SimplePlayerTrigger::new);
    public static final RegistrySupplier<SimplePlayerTrigger> STARGAZER = TRIGGERS.register("stargazer", SimplePlayerTrigger::new);
    public static final RegistrySupplier<SimplePlayerTrigger> CASH_REGISTER_TRADE = TRIGGERS.register("cash_register_trade", SimplePlayerTrigger::new);
    public static final RegistrySupplier<SimplePlayerTrigger> BIN_FLIES = TRIGGERS.register("bin_flies", SimplePlayerTrigger::new);
    public static final RegistrySupplier<SimplePlayerTrigger> PELLS_BROKEN = TRIGGERS.register("pells_broken", SimplePlayerTrigger::new);
    public static final RegistrySupplier<SimplePlayerTrigger> PELLS_WOOL = TRIGGERS.register("pells_wool", SimplePlayerTrigger::new);

    static {
        TRIGGERS.register();
    }
}
