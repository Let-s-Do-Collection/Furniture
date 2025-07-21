package com.berksire.furniture.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/// Taken from cph101/honig. Replace with proper implementation when upgraded to 1.21
public abstract class HierarchicalBlockModel<T extends BlockEntity> extends Model {
    private static final Vector3f ANIMATION_VECTOR_CACHE = new Vector3f();

    public HierarchicalBlockModel() {
        super(RenderType::entityCutoutNoCull);
    }

    public HierarchicalBlockModel(Function<ResourceLocation, RenderType> function) {
        super(function);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, int k) {
        this.root().render(poseStack, vertexConsumer, i, j, k);
    }

    public abstract ModelPart root();

    public Optional<ModelPart> getAnyDescendantWithName(String string) {
        return string.equals("root") ? Optional.of(this.root()) : this.root().getAllParts().filter((modelPart) -> modelPart.hasChild(string)).findFirst().map((modelPart) -> modelPart.getChild(string));
    }

    protected void animate(AnimationState animationState, AnimationDefinition animationDefinition, float f) {
        this.animate(animationState, animationDefinition, f, 1.0F);
    }

    protected void animate(AnimationState animationState, AnimationDefinition animationDefinition, float f, float g) {
        animationState.updateTime(f, g);
        animationState.ifStarted((animationStatex) -> animateBlock(this, animationDefinition, animationStatex.getAccumulatedTime(), 1.0F, ANIMATION_VECTOR_CACHE));
    }

    protected void applyStatic(AnimationDefinition animationDefinition) {
        animateBlock(this, animationDefinition, 0L, 1.0F, ANIMATION_VECTOR_CACHE);
    }

    public abstract void setupAnim(T entity, float ageInTicks);

    public static void animateBlock(HierarchicalBlockModel<?> hierarchicalModel, AnimationDefinition animationDefinition, long l, float f, Vector3f vector3f) {
        float g = KeyframeAnimations.getElapsedSeconds(animationDefinition, l);

        for(Map.Entry<String, List<AnimationChannel>> entry : animationDefinition.boneAnimations().entrySet()) {
            Optional<ModelPart> optional = hierarchicalModel.getAnyDescendantWithName(entry.getKey());
            List<AnimationChannel> list = entry.getValue();
            optional.ifPresent((modelPart) -> list.forEach((animationChannel) -> {
                Keyframe[] keyframes = animationChannel.keyframes();
                int i = Math.max(0, Mth.binarySearch(0, keyframes.length, (ix) -> g <= keyframes[ix].timestamp()) - 1);
                int j = Math.min(keyframes.length - 1, i + 1);
                Keyframe keyframe = keyframes[i];
                Keyframe keyframe2 = keyframes[j];
                float h = g - keyframe.timestamp();
                float k;
                if (j != i) {
                    k = Mth.clamp(h / (keyframe2.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
                } else {
                    k = 0.0F;
                }

                keyframe2.interpolation().apply(vector3f, k, keyframes, i, j, f);
                animationChannel.target().apply(modelPart, vector3f);
            }));
        }

    }
}
