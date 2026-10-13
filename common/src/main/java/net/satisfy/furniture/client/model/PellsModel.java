package net.satisfy.furniture.client.model;

import net.satisfy.furniture.core.entity.PellsEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class PellsModel<T extends Entity> extends EntityModel<T> {
    private final ModelPart Upper;
    private final ModelPart Head;
    private final ModelPart Body;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Stand;
    private final ModelPart Baseplate;

    public PellsModel(ModelPart root) {
        this.Upper = root.getChild("Upper");
        this.Head = Upper.getChild("Head");
        this.Body = Upper.getChild("Body");
        this.RightArm = Upper.getChild("RightArm");
        this.LeftArm = Upper.getChild("LeftArm");
        this.Stand = root.getChild("Stand");
        this.Baseplate = root.getChild("Baseplate");
    }

    public ModelPart getUpper() {
        return Upper;
    }

    public ModelPart getHead() {
        return Head;
    }

    public ModelPart getBody() {
        return Body;
    }

    public ModelPart getRightArm() {
        return RightArm;
    }

    public ModelPart getLeftArm() {
        return LeftArm;
    }

    @SuppressWarnings("unused")
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Upper = partdefinition.addOrReplaceChild("Upper", CubeListBuilder.create(), PartPose.offset(0.0F, 15.0F, 0.0F));
        PartDefinition Head = Upper.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 13).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -20.0F, 0.0F));
        PartDefinition Body = Upper.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(27, 24).addBox(-4.0F, -8.0F, -2.5F, 8.0F, 16.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 0.0F));
        PartDefinition RightArm = Upper.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(32, 13).addBox(-2.0F, -2.5F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -13.5F, 1.0F));
        PartDefinition LeftArm = Upper.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(32, 13).mirror().addBox(-2.0F, -2.5F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-6.0F, -13.5F, 1.0F));
        PartDefinition Stand = partdefinition.addOrReplaceChild("Stand", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -9.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition Baseplate = partdefinition.addOrReplaceChild("Baseplate", CubeListBuilder.create().texOffs(0, 0).addBox(-6.0F, -0.5F, -6.0F, 12.0F, 1.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 23.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float HeadPitch) {
        if (entityIn instanceof PellsEntity entity) {
            float partial = ageInTicks - entity.tickCount;
            float swayX = Mth.lerp(partial, entity.swayXO, entity.swayX);
            float swayZ = Mth.lerp(partial, entity.swayZO, entity.swayZ);
            float lag = Mth.lerp(partial, entity.headLagO, entity.headLag);
            float idle = Mth.sin(ageInTicks * 0.05F) * 0.015F;

            this.Upper.xRot = swayX;
            this.Upper.zRot = swayZ + idle;
            this.Head.xRot = (lag - swayX) * 0.8F;
            this.Head.zRot = -swayZ * 0.5F;
            this.LeftArm.xRot = -swayX * 1.4F;
            this.LeftArm.zRot = -swayZ * 1.2F - Math.abs(swayX) * 0.6F;
            this.RightArm.xRot = -swayX * 1.4F;
            this.RightArm.zRot = -swayZ * 1.2F + Math.abs(swayX) * 0.6F;

            float durability = entity.getDurabilityFraction();
            this.LeftArm.visible = durability > PellsEntity.LEFT_ARM_THRESHOLD;
            this.RightArm.visible = durability > PellsEntity.RIGHT_ARM_THRESHOLD;
            this.Head.visible = durability > PellsEntity.HEAD_THRESHOLD;
            this.Body.visible = durability > 0.0F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        Upper.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        Stand.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        Baseplate.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
