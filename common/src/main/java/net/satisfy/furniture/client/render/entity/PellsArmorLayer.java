package net.satisfy.furniture.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.satisfy.furniture.client.model.PellsModel;
import net.satisfy.furniture.core.entity.PellsEntity;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

//wtf man rewrite this whenever i have a lot of time :D //todo
public class PellsArmorLayer extends RenderLayer<PellsEntity, PellsModel<PellsEntity>> {
    private static final float HELMET_SCALE = 1.0F;
    private static final float HELMET_Y = 0.0F;
    private static final float CHESTPLATE_SCALE = 1.0F;
    private static final float CHESTPLATE_Y = 0.0F;
    private static final float LEGGINGS_SCALE = 1.0F;
    private static final float LEGGINGS_Y = 0.0F;
    private static final float BOOTS_SCALE = 1.0F;
    private static final float BOOTS_Y = 0.0F;

    private static final float LEG_DEPTH_OFFSET = 0.98F;

    private final Vector3f headPivot = new Vector3f();
    private final Vector3f bodyPivot = new Vector3f();
    private final Vector3f rightArmPivot = new Vector3f();
    private final Vector3f leftArmPivot = new Vector3f();
    private final Quaternionf upperRotation = new Quaternionf();
    private final HumanoidModel<PellsEntity> pose;
    private final HumanoidArmorLayer<PellsEntity, HumanoidModel<PellsEntity>, HumanoidModel<PellsEntity>> armor;

    public PellsArmorLayer(RenderLayerParent<PellsEntity, PellsModel<PellsEntity>> parent, EntityRendererProvider.Context context) {
        super(parent);
        this.pose = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        this.pose.young = false;
        RenderLayerParent<PellsEntity, HumanoidModel<PellsEntity>> poseParent = new RenderLayerParent<>() {
            @Override
            public @NotNull HumanoidModel<PellsEntity> getModel() {
                return pose;
            }

            @Override
            public @NotNull ResourceLocation getTextureLocation(PellsEntity entity) {
                return parent.getTextureLocation(entity);
            }
        };
        HumanoidModel<PellsEntity> inner = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR));
        HumanoidModel<PellsEntity> outer = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
        this.armor = new HumanoidArmorLayer<>(poseParent, inner, outer, context.getModelManager()) {
            @Override
            protected void setPartVisibility(@NotNull HumanoidModel<PellsEntity> model, @NotNull EquipmentSlot slot) {
                super.setPartVisibility(model, slot);
                switch (slot) {
                    case HEAD -> {
                        adjust(HELMET_SCALE, HELMET_Y, model.head, model.hat);
                        anchor(model.head, headPivot);
                        anchor(model.hat, headPivot);
                    }
                    case CHEST -> {
                        scaleAround(model.body, CHESTPLATE_SCALE, model.rightArm, model.leftArm);
                        adjust(CHESTPLATE_SCALE, CHESTPLATE_Y, model.body, model.rightArm, model.leftArm);
                        anchor(model.body, bodyPivot);
                        anchor(model.rightArm, rightArmPivot);
                        anchor(model.leftArm, leftArmPivot);
                    }
                    case LEGS -> {
                        adjust(LEGGINGS_SCALE, LEGGINGS_Y, model.body);
                        anchor(model.body, bodyPivot);
                        model.rightLeg.visible = false;
                        model.leftLeg.visible = false;
                    }
                    case FEET -> {
                        adjust(BOOTS_SCALE, BOOTS_Y, model.rightLeg, model.leftLeg);
                        separateLegs(model, BOOTS_SCALE);
                        fixToGround(model.rightLeg);
                        fixToGround(model.leftLeg);
                    }
                    default -> {
                    }
                }
            }
        };
    }

    private static void scaleAround(ModelPart center, float scale, ModelPart... parts) {
        for (ModelPart part : parts) {
            part.x = center.x + (part.x - center.x) * scale;
            part.y = center.y + (part.y - center.y) * scale;
            part.z = center.z + (part.z - center.z) * scale;
        }
    }

    private static void anchor(ModelPart part, Vector3f pivot) {
        Vector3f offset = new Vector3f(part.x - pivot.x, part.y - pivot.y, part.z - pivot.z);
        new Quaternionf().rotationZYX(part.zRot, part.yRot, part.xRot).transform(offset);
        part.setPos(pivot.x + offset.x, pivot.y + offset.y, pivot.z + offset.z);
    }

    private void fixToGround(ModelPart part) {
        Vector3f position = new Vector3f(part.x, part.y, part.z);
        new Quaternionf(upperRotation).conjugate().transform(position);
        part.setPos(position.x, position.y, position.z);
        Vector3f angles = new Quaternionf(upperRotation).conjugate().getEulerAnglesZYX(new Vector3f());
        part.setRotation(angles.x, angles.y, angles.z);
    }

    private static void separateLegs(HumanoidModel<PellsEntity> model, float scale) {
        model.rightLeg.x *= scale;
        model.leftLeg.x *= scale;
        model.leftLeg.zScale *= LEG_DEPTH_OFFSET;
    }

    private static void adjust(float scale, float y, ModelPart... parts) {
        for (ModelPart part : parts) {
            part.xScale = scale;
            part.yScale = scale;
            part.zScale = scale;
            part.y += y;
        }
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, PellsEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        PellsModel<PellsEntity> model = getParentModel();
        if (!model.getBody().visible) {
            return;
        }
        ModelPart upper = model.getUpper();
        upperRotation.rotationZYX(upper.zRot, upper.yRot, upper.xRot);
        ModelPart head = model.getHead();
        ModelPart body = model.getBody();
        ModelPart rightArm = model.getLeftArm();
        ModelPart leftArm = model.getRightArm();
        headPivot.set(head.x, head.y, head.z);
        bodyPivot.set(body.x, body.y, body.z);
        rightArmPivot.set(rightArm.x, rightArm.y, rightArm.z);
        leftArmPivot.set(leftArm.x, leftArm.y, leftArm.z);
        pose.head.copyFrom(head);
        pose.head.y += 4.0F;
        pose.hat.copyFrom(pose.head);
        pose.body.copyFrom(body);
        pose.body.y -= 8.0F;
        pose.rightArm.copyFrom(rightArm);
        pose.rightArm.x += 1.0F;
        pose.rightArm.y -= 0.5F;
        pose.leftArm.copyFrom(leftArm);
        pose.leftArm.x -= 1.0F;
        pose.leftArm.y -= 0.5F;
        pose.rightLeg.setPos(-1.9F, -3.0F, 0.0F);
        pose.rightLeg.setRotation(0.0F, 0.0F, 0.0F);
        pose.leftLeg.setPos(1.9F, -3.0F, 0.0F);
        pose.leftLeg.setRotation(0.0F, 0.0F, 0.0F);
        poseStack.pushPose();
        upper.translateAndRotate(poseStack);
        armor.render(poseStack, buffer, packedLight, entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
        poseStack.popPose();
    }
}
