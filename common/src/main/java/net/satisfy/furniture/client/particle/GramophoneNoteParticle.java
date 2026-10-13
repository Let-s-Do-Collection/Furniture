package net.satisfy.furniture.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class GramophoneNoteParticle extends TextureSheetParticle {
    private static final int FORWARD_TICKS = 12;
    private final double dirX;
    private final double dirZ;

    protected GramophoneNoteParticle(ClientLevel level, double x, double y, double z, double dirX, double hue, double dirZ, SpriteSet sprites) {
        super(level, x, y, z);
        this.dirX = dirX;
        this.dirZ = dirZ;
        this.lifetime = 30 + this.random.nextInt(8);
        this.gravity = 0.0F;
        this.hasPhysics = false;
        this.quadSize *= 1.5F;
        this.rCol = Math.max(0.0F, Mth.sin(((float) hue + 0.0F) * Mth.TWO_PI) * 0.65F + 0.35F);
        this.gCol = Math.max(0.0F, Mth.sin(((float) hue + 0.33333334F) * Mth.TWO_PI) * 0.65F + 0.35F);
        this.bCol = Math.max(0.0F, Mth.sin(((float) hue + 0.6666667F) * Mth.TWO_PI) * 0.65F + 0.35F);
        this.pickSprite(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }
        float forward = Math.max(0.0F, 1.0F - (float) this.age / FORWARD_TICKS) * 0.07F;
        float up = Mth.clamp((this.age - FORWARD_TICKS * 0.5F) / FORWARD_TICKS, 0.0F, 1.0F) * 0.05F;
        this.move(this.dirX * forward, up, this.dirZ * forward);
        this.alpha = Math.min(1.0F, (this.lifetime - this.age) / 6.0F);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new GramophoneNoteParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
