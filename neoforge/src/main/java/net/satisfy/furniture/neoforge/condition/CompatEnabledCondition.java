package net.satisfy.furniture.neoforge.condition;

import net.satisfy.furniture.Furniture;
import net.satisfy.furniture.platform.PlatformHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public record CompatEnabledCondition(String mod, String furniture) implements ICondition {
    public static final MapCodec<CompatEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("mod").forGetter(CompatEnabledCondition::mod),
            Codec.STRING.optionalFieldOf("furniture", "").forGetter(CompatEnabledCondition::furniture)
    ).apply(instance, CompatEnabledCondition::new));
    private static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, Furniture.MOD_ID);

    static {
        CONDITION_CODECS.register("compat_enabled", () -> CODEC);
    }

    public static void register(IEventBus modEventBus) {
        CONDITION_CODECS.register(modEventBus);
    }

    @Override
    public boolean test(IContext context) {
        return PlatformHelper.isCompatAllowed(mod, furniture);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
