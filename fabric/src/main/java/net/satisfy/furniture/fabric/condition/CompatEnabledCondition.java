package net.satisfy.furniture.fabric.condition;

import net.satisfy.furniture.Furniture;
import net.satisfy.furniture.platform.PlatformHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

public record CompatEnabledCondition(String mod, String furniture) implements ResourceCondition {
    public static final MapCodec<CompatEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("mod").forGetter(CompatEnabledCondition::mod),
            Codec.STRING.optionalFieldOf("furniture", "").forGetter(CompatEnabledCondition::furniture)
    ).apply(instance, CompatEnabledCondition::new));
    public static final ResourceConditionType<CompatEnabledCondition> TYPE = ResourceConditionType.create(Furniture.identifier("compat_enabled"), CODEC);

    public static void register() {
        ResourceConditions.register(TYPE);
    }

    @Override
    public ResourceConditionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean test(@Nullable HolderLookup.Provider registryLookup) {
        return PlatformHelper.isCompatAllowed(mod, furniture);
    }
}
