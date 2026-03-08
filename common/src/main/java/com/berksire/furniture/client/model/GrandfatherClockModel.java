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
import net.minecraft.world.entity.Entity;

public class GrandfatherClockModel<T extends Entity> extends EntityModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Furniture.identifier("grandfather_clock"), "main");

    private final ModelPart grandfatherClock;
    public final ModelPart pendulum;
    public final ModelPart minutes;
    public final ModelPart hours;
    public final ModelPart inner;

    public GrandfatherClockModel(ModelPart root) {
        this.grandfatherClock = root.getChild("grandfather_clock");
        this.pendulum = this.grandfatherClock.getChild("pendulum");
        ModelPart clock = this.grandfatherClock.getChild("clock");
        this.minutes = clock.getChild("minutes");
        this.hours = clock.getChild("hours");
        this.inner = this.grandfatherClock.getChild("inner");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition root = meshDefinition.getRoot();

        PartDefinition grandfatherClock = root.addOrReplaceChild("grandfather_clock", CubeListBuilder.create()
                .texOffs(48, 49).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-6.0F, 8.0F, 0.0F, 12.0F, 26.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(2, 1).addBox(-5.0F, 8.0F, 0.0F, 10.0F, 17.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(0, 64).addBox(-7.0F, 25.0F, -1.0F, 14.0F, 2.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(0, 64).addBox(-7.0F, 6.0F, -1.0F, 14.0F, 2.0F, 13.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(-6.0F, -4.0F, 0.0F, 12.0F, 10.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(48, 15).addBox(-6.0F, -9.0F, 0.0F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -10.0F, -4.0F));

        grandfatherClock.addOrReplaceChild("pendulum", CubeListBuilder.create()
                .texOffs(48, 44).addBox(-2.0F, 9.0F, -1.0F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(48, 32).addBox(-1.0F, -3.0F, 0.0F, 2.0F, 12.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition clock = grandfatherClock.addOrReplaceChild("clock", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        clock.addOrReplaceChild("minutes", CubeListBuilder.create()
                .texOffs(52, 37).addBox(-0.5F, -3.5F, -0.25F, 1.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -34.0F, -4.0F));

        clock.addOrReplaceChild("hours", CubeListBuilder.create()
                .texOffs(52, 32).addBox(-0.5F, -4.5F, -0.25F, 1.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -34.0F, -4.0F));

        grandfatherClock.addOrReplaceChild("inner", CubeListBuilder.create()
                        .texOffs(25, 1).addBox(5.0F, -26.0F, -4.0F, 0.0F, 17.0F, 11.0F, new CubeDeformation(0.0F))
                
                        .texOffs(37, 12).addBox(-5.0F, -26.0F, 7.0F, 10.0F, 17.0F, 0.0F, new CubeDeformation(0.0F))
                        .texOffs(25, 1).addBox(-5.0F, -26.0F, -4.0F, 0.0F, 17.0F, 11.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));


        return LayerDefinition.create(meshDefinition, 96, 96);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
        this.grandfatherClock.render(poseStack, vertexConsumer, i, j, k);
    }
}