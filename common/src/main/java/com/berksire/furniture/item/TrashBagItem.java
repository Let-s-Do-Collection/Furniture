package com.berksire.furniture.item;

import com.berksire.furniture.registry.TagRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class TrashBagItem extends Item {
    private static final int COOLDOWN_TICKS = 1200;
    private static final Map<UUID, Long> cooldowns = new HashMap<>();

    public TrashBagItem(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        /*
        if (world.isClientSide || user.isCrouching()) {
            return super.use(world, user, hand);
        }

        UUID userUUID = user.getUUID();
        long currentTime = world.getGameTime();
        if (!user.isCreative() && cooldowns.containsKey(userUUID) && (currentTime - cooldowns.get(userUUID)) < COOLDOWN_TICKS) {
            user.displayClientMessage(Component.translatable("tooltip.furniture.trash_bag.cooldown").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(user.getItemInHand(hand));
        }

        ItemStack itemStack = user.getItemInHand(hand);

        Random random = new Random();
        List<Item> items = BuiltInRegistries.ITEM.stream()
                .filter(item -> {
                    String itemName = BuiltInRegistries.ITEM.getKey(item).getPath();
                    return !item.builtInRegistryHolder().is(TagRegistry.TRASH_BAG_BLACKLIST)
                            && !itemName.contains("book")
                            && !itemName.contains("command_block")
                            && !itemName.contains("creative")
                            && !itemName.contains("debug")
                            && !itemName.contains("diamond")
                            && !itemName.contains("egg")
                            && !itemName.contains("ender")
                            && !itemName.contains("map")
                            && !itemName.contains("netherite")
                            && !itemName.contains("netherstar")
                            && !itemName.contains("ore")
                            && !itemName.contains("shulker")
                            && !itemName.contains("spawn_egg")
                            && !itemName.contains("spawn_keg")
                            && !itemName.contains("worldshaper")
                            && !itemName.contains("_head")
                            && !itemName.contains("dragon")
                            && !itemName.contains("light");
                })
                .toList();

        Item randomItem = items.get(random.nextInt(items.size()));
        ItemStack spawnedItem = new ItemStack(randomItem);

        world.addFreshEntity(new ItemEntity(world, user.getX(), user.getY(), user.getZ(), spawnedItem));

        if (!user.isCreative()) {
            itemStack.shrink(1);
            cooldowns.put(userUUID, currentTime);
        }

        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());*/
        return super.use(world, user, hand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("tooltip.furniture.trash_bag").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
    }
}
