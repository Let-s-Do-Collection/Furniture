package net.satisfy.furniture.core.loot;

import net.satisfy.furniture.core.registry.LootFunctionRegistry;
import net.satisfy.furniture.core.registry.TagRegistry;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RandomItemFunction extends LootItemConditionalFunction {
    public static final MapCodec<RandomItemFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).apply(instance, RandomItemFunction::new));
    private static final List<String> EXCLUDED_NAMES = List.of(
            "command_block", "creative", "debug", "spawn_egg", "spawn_keg", "worldshaper", "_head", "shulker", "netherite", "nether_star", "dragon"
    );

    protected RandomItemFunction(List<LootItemCondition> conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ItemStack run(ItemStack stack, LootContext context) {
        List<Item> candidates = BuiltInRegistries.ITEM.stream()
                .filter(item -> item != Items.AIR && isAllowed(item) && item.isEnabled(context.getLevel().enabledFeatures()))
                .toList();
        if (candidates.isEmpty()) {
            return stack;
        }
        return new ItemStack(candidates.get(context.getRandom().nextInt(candidates.size())));
    }

    private static boolean isAllowed(Item item) {
        if (BuiltInRegistries.ITEM.wrapAsHolder(item).is(TagRegistry.TRASH_BAG_BLACKLIST)) {
            return false;
        }
        String name = BuiltInRegistries.ITEM.getKey(item).getPath();
        return EXCLUDED_NAMES.stream().noneMatch(name::contains);
    }

    @Override
    public @NotNull LootItemFunctionType<RandomItemFunction> getType() {
        return LootFunctionRegistry.RANDOM_ITEM.get();
    }
}
