package net.satisfy.furniture.client.overlay;

import net.satisfy.furniture.platform.PlatformHelper;
import net.satisfy.furniture.core.entity.PellsEntity;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import org.joml.Vector2i;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.List;

//TODO: move to the Foundation BlockInfoOverlay once it supports entities
public final class PellsHud {
    private static final ResourceLocation HEART = ResourceLocation.withDefaultNamespace("hud/heart/full");
    private static final ResourceLocation ARMOR = ResourceLocation.withDefaultNamespace("hud/armor_full");
    private static final String ICON_SPACE = "   ";

    private PellsHud() {
    }

    public static void init() {
        ClientGuiEvent.RENDER_HUD.register(PellsHud::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!PlatformHelper.showPellsInfo() || minecraft.options.hideGui || minecraft.screen != null || !(minecraft.hitResult instanceof EntityHitResult hit) || !(hit.getEntity() instanceof PellsEntity pells)) {
            return;
        }
        float fraction = pells.getDurabilityFraction();
        boolean damaged = fraction <= PellsEntity.REPAIR_HINT_THRESHOLD;
        List<Component> lines = new ArrayList<>();
        List<ResourceLocation> icons = new ArrayList<>();
        if (damaged) {
            lines.add(Component.translatable(fraction <= 0.0F ? "hud.furniture.pells.destroyed" : fraction <= 0.05F ? "hud.furniture.pells.broken" : "hud.furniture.pells.almost_broken").withStyle(ChatFormatting.RED));
            icons.add(null);
        }
        if (PlatformHelper.showPellsStats() && isWeapon(minecraft.player.getMainHandItem())) {
            lines.add(Component.literal(ICON_SPACE).append(Component.translatable("hud.furniture.pells.health", Mth.ceil(pells.getDurability()), Mth.ceil(PellsEntity.maxDurability()))));
            icons.add(HEART);
            if (pells.getArmorValue() > 0) {
                lines.add(Component.literal(ICON_SPACE).append(Component.translatable("hud.furniture.pells.armor", pells.getArmorValue())));
                icons.add(ARMOR);
            }
        }
        if (lines.isEmpty()) {
            return;
        }
        if (damaged) {
            lines.add(Component.translatable("hud.furniture.pells.repair").withStyle(ChatFormatting.GRAY));
            icons.add(null);
        }
        lines.add(Component.translatable("hud.furniture.pells.remove").withStyle(ChatFormatting.GRAY));
        icons.add(null);
        int x = graphics.guiWidth() / 2 + 8;
        int y = graphics.guiHeight() / 2 + 4;
        Vector2i position = new Vector2i(x + 12, y - 12);
        ClientTooltipPositioner positioner = (screenWidth, screenHeight, mouseX, mouseY, width, height) -> {
            Vector2ic placed = DefaultTooltipPositioner.INSTANCE.positionTooltip(screenWidth, screenHeight, mouseX, mouseY, width, height);
            position.set(placed);
            return placed;
        };
        graphics.renderTooltip(minecraft.font, lines.stream().map(Component::getVisualOrderText).toList(), positioner, x, y);
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 500.0F);
        for (int i = 0; i < icons.size(); i++) {
            ResourceLocation icon = icons.get(i);
            if (icon != null) {
                graphics.blitSprite(icon, position.x(), position.y() + i * 10 + (i > 0 ? 2 : 0) - 1, 9, 9);
            }
        }
        graphics.pose().popPose();
    }

    private static boolean isWeapon(ItemStack stack) {
        return stack.is(ItemTags.SWORDS) || stack.is(ItemTags.AXES) || stack.is(Items.TRIDENT) || stack.is(Items.MACE);
    }
}
