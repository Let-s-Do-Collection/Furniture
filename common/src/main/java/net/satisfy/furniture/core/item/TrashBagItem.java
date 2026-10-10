package net.satisfy.furniture.core.item;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.Furniture;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.ChatFormatting;
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

import java.util.List;

public class TrashBagItem extends Item {
    public static final ResourceKey<LootTable> LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, Furniture.identifier("gameplay/trash_bag"));

    public TrashBagItem(Properties properties) {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (world.isClientSide || user.isCrouching()) {
            return super.use(world, user, hand);
        }

        ItemStack itemStack = user.getItemInHand(hand);
        if (world instanceof ServerLevel serverLevel) {
            LootTable lootTable = serverLevel.getServer().reloadableRegistries().getLootTable(LOOT_TABLE);
            LootParams params = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, user.position())
                    .withParameter(LootContextParams.THIS_ENTITY, user)
                    .withLuck(user.getLuck())
                    .create(LootContextParamSets.GIFT);
            for (ItemStack loot : lootTable.getRandomItems(params)) {
                world.addFreshEntity(new ItemEntity(world, user.getX(), user.getY(), user.getZ(), loot));
            }
        }
        if (!user.isCreative()) {
            itemStack.shrink(1);
            if (PlatformHelper.isTrashBagCooldownEnabled() && PlatformHelper.getTrashBagCooldownSeconds() > 0) {
                user.getCooldowns().addCooldown(this, PlatformHelper.getTrashBagCooldownSeconds() * 20);
            }
        }

        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.BUNDLE_INSERT, SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("tooltip.furniture.trash_bag").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
    }
}
