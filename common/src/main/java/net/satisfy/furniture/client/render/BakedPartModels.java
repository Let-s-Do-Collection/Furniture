package net.satisfy.furniture.client.render;

import net.satisfy.furniture.Furniture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Function;

@Environment(EnvType.CLIENT)
public final class BakedPartModels {
    public static final ResourceLocation TELESCOPE_TUBE = Furniture.identifier("block/telescope_tube");
    public static final ResourceLocation CASH_REGISTER_DRAWER = Furniture.identifier("block/cash_register_drawer");
    public static final ResourceLocation BOAT_IN_A_JAR_JAR = Furniture.identifier("block/boat_in_a_jar_jar");
    public static final ResourceLocation BOAT_IN_A_JAR_WATER = Furniture.identifier("block/boat_in_a_jar_water");
    public static final ResourceLocation BOAT_IN_A_JAR_BOAT = Furniture.identifier("block/boat_in_a_jar_boat");
    public static final ResourceLocation BOAT_IN_A_JAR_BOAT_BROKEN = Furniture.identifier("block/boat_in_a_jar_boat_broken");
    public static final List<ResourceLocation> ALL = List.of(TELESCOPE_TUBE, CASH_REGISTER_DRAWER, BOAT_IN_A_JAR_JAR, BOAT_IN_A_JAR_WATER, BOAT_IN_A_JAR_BOAT, BOAT_IN_A_JAR_BOAT_BROKEN);

    public static Function<ResourceLocation, BakedModel> lookup = id -> Minecraft.getInstance().getModelManager().getMissingModel();

    private BakedPartModels() {
    }

    public static BakedModel get(ResourceLocation id) {
        return lookup.apply(id);
    }
}
