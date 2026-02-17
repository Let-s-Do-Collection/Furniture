package com.berksire.furniture.client.model;

import com.berksire.furniture.Furniture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ClockModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Furniture.identifier("clock"), "main");

    private final ModelPart clock;
    private final ModelPart minutes;
    private final ModelPart hours;

    public ClockModel(ModelPart root) {
        this.clock = root.getChild("clock");
        this.minutes = this.clock.getChild("minutes");
        this.hours = this.clock.getChild("hours");
    }

    public void setHandRotations(float minutesRotation, float hoursRotation) {
        this.minutes.zRot = minutesRotation;
        this.hours.zRot = hoursRotation;
    }

    @SuppressWarnings("unused")
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition clock = partdefinition.addOrReplaceChild("clock", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition base = clock.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 16).addBox(-13.0F, -14.0F, -1.0F, 14.0F, 14.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(6.0F, -1.0F, 7.0F));

        PartDefinition clockwork = clock.addOrReplaceChild("clockwork", CubeListBuilder.create().texOffs(2, 13).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 5.5F));

        PartDefinition minutes = clock.addOrReplaceChild("minutes", CubeListBuilder.create().texOffs(0, 12).addBox(-0.5F, -5.5F, 0.0F, 1.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 5.5F));

        PartDefinition hours = clock.addOrReplaceChild("hours", CubeListBuilder.create().texOffs(0, 12).addBox(-0.5F, -4.5F, 0.0F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -8.0F, 5.5F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
        clock.render(poseStack, vertexConsumer, i, j, k);
    }
}