package net.satisfy.furniture.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@Environment(EnvType.CLIENT)
public final class ShaderPackHelper {
    private static final MethodHandle SHADOW_PASS = find("isRenderingShadowPass");

    private ShaderPackHelper() {
    }

    public static boolean isRenderingShadowPass() {
        return call(SHADOW_PASS);
    }

    private static boolean call(MethodHandle handle) {
        if (handle == null) return false;
        try {
            return (boolean) handle.invoke();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static MethodHandle find(String name) {
        try {
            Class<?> api = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            Object instance = lookup.findStatic(api, "getInstance", MethodType.methodType(api)).invoke();
            return lookup.findVirtual(api, name, MethodType.methodType(boolean.class)).bindTo(instance);
        } catch (Throwable ignored) {
            return null;
        }
    }
}
