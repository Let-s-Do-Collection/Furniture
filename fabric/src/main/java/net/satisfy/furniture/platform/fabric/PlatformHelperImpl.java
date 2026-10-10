package net.satisfy.furniture.platform.fabric;

import net.satisfy.furniture.fabric.config.FurnitureFabricConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class PlatformHelperImpl {
    private static FurnitureFabricConfig config() {
        return AutoConfig.getConfigHolder(FurnitureFabricConfig.class).getConfig();
    }

    public static boolean isCompatEnabled(String modId) {
        FurnitureFabricConfig.CompatSettings compat = config().compat;
        if (!compat.enableCompat) {
            return false;
        }
        return switch (modId) {
            case "bloomingnature" -> compat.bloomingNature;
            case "meadow" -> compat.meadow;
            case "beachparty" -> compat.beachparty;
            case "alpinewhispers" -> compat.alpineWhispers;
            case "vinery" -> compat.vinery;
            default -> true;
        };
    }

    public static boolean showFishTankInfo() {
        return config().infoTooltips.showFishTankInfo;
    }

    public static boolean showWardrobeInfo() {
        return config().infoTooltips.showWardrobeInfo;
    }

    public static boolean showBinInfo() {
        return config().infoTooltips.showBinInfo;
    }

    public static boolean showTelescopeInfo() {
        return config().infoTooltips.showTelescopeInfo;
    }

    public static boolean showLampInfo() {
        return config().infoTooltips.showLampInfo;
    }

    public static boolean showCashRegisterInfo() {
        return config().infoTooltips.showCashRegisterInfo;
    }

    public static boolean showSteamVentInfo() {
        return config().infoTooltips.showSteamVentInfo;
    }

    public static boolean showBoatInAJarInfo() {
        return config().infoTooltips.showBoatInAJarInfo;
    }

    public static boolean showPellsInfo() {
        return config().infoTooltips.showPellsInfo;
    }

    public static boolean showPellsStats() {
        return config().infoTooltips.showPellsStats;
    }

    public static boolean areMirrorReflectionsEnabled() {
        return config().mirror.enableReflections;
    }

    public static int getMirrorRenderDistance() {
        return config().mirror.renderDistance;
    }

    public static int getMaxMirrors() {
        return config().mirror.maxMirrors;
    }

    public static int getCashRegisterMerchantRange() {
        return config().cashRegister.merchantRange;
    }

    public static int getCashRegisterAutoCloseSeconds() {
        return config().cashRegister.autoCloseSeconds;
    }

    public static boolean isBoatInAJarShakingEnabled() {
        return config().boatInAJar.enableShaking;
    }

    public static boolean canBoatInAJarBreak() {
        return config().boatInAJar.enableBreaking;
    }

    public static int getPellsMaxDurability() {
        return config().pells.maxDurability;
    }

    public static int getPellsWheatRepair() {
        return config().pells.wheatRepair;
    }

    public static int getBinBagDropChance() {
        return config().bin.bagDropChance;
    }

    public static boolean isBinStinkEnabled() {
        return config().bin.enableStink;
    }

    public static boolean areBinFliesEnabled() {
        return config().bin.enableFlies;
    }

    public static boolean isTrashBagCooldownEnabled() {
        return config().bin.enableTrashBagCooldown;
    }

    public static int getTrashBagCooldownSeconds() {
        return config().bin.trashBagCooldownSeconds;
    }

    public static int getSteamVentSourceRange() {
        return config().steam.ventSourceRange;
    }

    public static int getSteamMaxPipeLength() {
        return config().steam.maxPipeLength;
    }

    public static boolean areShootingStarsEnabled() {
        return config().telescope.enableShootingStars;
    }

    public static int getShootingStarChance() {
        return config().telescope.shootingStarRarity;
    }

    public static boolean isDisplayRotationEnabled() {
        return config().display.enableRotation;
    }

    public static boolean isDisplayBobbingEnabled() {
        return config().display.enableBobbing;
    }

    public static double getTelescopeMinMagnification() {
        return Math.max(1.0, Math.min(100.0, config().telescope.minMagnification));
    }

    public static double getTelescopeMaxMagnification() {
        return Math.max(1.0, Math.min(100.0, config().telescope.maxMagnification));
    }

    public static int getTelescopeMaxDistance() {
        return config().telescope.maxDistance;
    }

    public static int getBoatInAJarBreakThreshold() {
        return config().boatInAJar.breakThreshold;
    }

    public static int getBoatInAJarAnimationSpeed() {
        return config().boatInAJar.animationSpeed;
    }

    public static boolean isCompatBenchesEnabled() {
        return config().compat.benches;
    }

    public static boolean isCompatCabinetsEnabled() {
        return config().compat.cabinets;
    }

    public static boolean isCompatClocksEnabled() {
        return config().compat.clocks;
    }

    public static boolean isCompatGrandfatherClocksEnabled() {
        return config().compat.grandfatherClocks;
    }

    public static boolean isCompatMirrorsEnabled() {
        return config().compat.mirrors;
    }

    public static boolean isCompatDeskChairsEnabled() {
        return config().compat.deskChairs;
    }

    public static boolean isCompatShuttersEnabled() {
        return config().compat.shutters;
    }

    public static boolean isCompatDesksEnabled() {
        return config().compat.desks;
    }

    public static boolean isCompatDressersEnabled() {
        return config().compat.dressers;
    }

    public static boolean isCompatWardrobesEnabled() {
        return config().compat.wardrobes;
    }

    public static boolean isCompatCurtainsEnabled() {
        return config().compat.curtains;
    }

    public static boolean isCompatPouffesEnabled() {
        return config().compat.pouffes;
    }

    public static boolean isCompatLampsEnabled() {
        return config().compat.lamps;
    }

    public static boolean isChimneySootEnabled() {
        return config().chimney.enableSoot;
    }

    public static int getChimneySootChance() {
        return config().chimney.sootRarity;
    }

    public static boolean isGrandfatherClockChimeEnabled() {
        return config().clock.enableChime;
    }

    public static boolean isClockTickingEnabled() {
        return config().clock.enableTicking;
    }

    public static int getClockTickingVolume() {
        return config().clock.tickingVolume;
    }

    public static boolean isFishTankAmbienceEnabled() {
        return config().misc.enableFishTankAmbience;
    }

    public static int getCofferVolume() {
        return config().misc.cofferVolume;
    }

    public static boolean areSteamPipeParticlesEnabled() {
        return config().steam.enablePipeParticles;
    }

    public static boolean areSteamPipeSoundsEnabled() {
        return config().steam.enablePipeSounds;
    }

    public static boolean areSteamVentParticlesEnabled() {
        return config().steam.enableVentParticles;
    }

    public static int getBinFlyFrequency() {
        return config().bin.flyFrequency;
    }

    public static int getBinFlyLifetime() {
        return config().bin.flyLifetime;
    }

    public static boolean isMirrorBlurEnabled() {
        return config().mirror.enableBlur;
    }

    public static int getMirrorReflectionRange() {
        return config().mirror.reflectionRange;
    }

    public static boolean isGrandfatherClockMidnightStrikeEnabled() {
        return config().clock.enableMidnightStrike;
    }

    public static boolean isGrandfatherClockStartleMobsEnabled() {
        return config().clock.enableStartleMobs;
    }

    public static boolean isMirrorCreeperPrankEnabled() {
        return config().mirror.enableCreeperPrank;
    }

    public static boolean isBoatInAJarStormEnabled() {
        return config().boatInAJar.enableStorm;
    }

    public static boolean isBoatInAJarThunderRockingEnabled() {
        return config().boatInAJar.enableThunderRocking;
    }

    public static boolean isPouffeBounceEnabled() {
        return config().pouffe.enableBounce;
    }

    public static int getPouffeBounceStrength() {
        return config().pouffe.bounceStrength;
    }

    public static int getPouffeFallDamage() {
        return config().pouffe.fallDamage;
    }

    public static boolean isGramophoneEerieElevenEnabled() {
        return config().gramophone.enableEerieEleven;
    }

    public static boolean isGramophoneHornPulseEnabled() {
        return config().gramophone.enableHornPulse;
    }

    public static boolean isFishTankCatWatchingEnabled() {
        return config().fishTank.enableCatsWatchFish;
    }
}
