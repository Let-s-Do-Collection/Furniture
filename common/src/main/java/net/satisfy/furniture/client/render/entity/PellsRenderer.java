package net.satisfy.furniture.client.render.entity;

import net.satisfy.furniture.Furniture;
import net.satisfy.furniture.core.entity.PellsEntity;
import net.satisfy.furniture.client.model.PellsModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PellsRenderer extends MobRenderer<PellsEntity, PellsModel<PellsEntity>>{
    protected static final ResourceLocation TEXTURE = Furniture.identifier("textures/entity/pells.png");
    private static final DecimalFormat FORMAT = new DecimalFormat("###.##", new DecimalFormatSymbols(Locale.ROOT));

    public PellsRenderer(EntityRendererProvider.Context context){
        super(context, new PellsModel<>(PellsModel.createBodyLayer().bakeRoot()), 0.0F);
        this.addLayer(new PellsArmorLayer(this, context));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(PellsEntity entity){
        return TEXTURE;
    }

    @Override
    public void render(PellsEntity entityIn, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn){
        super.render(entityIn, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        if (entityIn.popups.isEmpty()) {
            return;
        }
        PellsEntity.DamagePopup popup = entityIn.popups.get(entityIn.popups.size() - 1);
        float t = popup.progress(partialTicks);
        float rise = 1.0F - (1.0F - t) * (1.0F - t);
        float alpha = t < 0.7F ? 1.0F : 1.0F - (t - 0.7F) / 0.3F;
        drawLabel(Component.literal(FORMAT.format(popup.amount)), entityIn.getBbHeight() + 0.4D + rise * 0.4D, 0.0F, 0.0F, 1.0F, 0xFFFFFFFF, alpha, stack, bufferIn);
    }

    private void drawLabel(Component text, double y, float offsetX, float offsetZ, float scale, int color, float alpha, PoseStack stack, MultiBufferSource buffer){
        if (alpha <= 0.02F) {
            return;
        }
        int argb = ((int) (Mth.clamp(alpha, 0.1F, 1.0F) * 255.0F) << 24) | (color & 0x00FFFFFF);
        stack.pushPose();
        stack.translate(offsetX, y, offsetZ);
        stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        stack.scale(0.025F * scale, -0.025F * scale, 0.025F * scale);
        Matrix4f matrix = stack.last().pose();
        Font font = this.getFont();
        float x = -font.width(text) / 2.0F;
        font.drawInBatch(text, x, 0.0F, argb, true, matrix, buffer, Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
        stack.popPose();
    }
}
