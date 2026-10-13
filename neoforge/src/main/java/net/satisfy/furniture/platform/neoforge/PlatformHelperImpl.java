package net.satisfy.furniture.platform.neoforge;

import net.satisfy.furniture.neoforge.config.FurnitureNeoForgeConfig;

public class PlatformHelperImpl {
    public static boolean isCompatEnabled(String modId) {
        if (!FurnitureNeoForgeConfig.ENABLE_COMPAT.get()) {
            return false;
        }
        return switch (modId) {
            case "bloomingnature" -> FurnitureNeoForgeConfig.COMPAT_BLOOMING_NATURE.get();
            case "meadow" -> FurnitureNeoForgeConfig.COMPAT_MEADOW.get();
            case "beachparty" -> FurnitureNeoForgeConfig.COMPAT_BEACHPARTY.get();
            case "alpinewhispers" -> FurnitureNeoForgeConfig.COMPAT_ALPINE_WHISPERS.get();
            case "vinery" -> FurnitureNeoForgeConfig.COMPAT_VINERY.get();
            default -> true;
        };
    }

    public static boolean showFishTankInfo() {
        return FurnitureNeoForgeConfig.showFishTankInfo;
    }

    public static boolean showWardrobeInfo() {
        return FurnitureNeoForgeConfig.showWardrobeInfo;
    }

    public static boolean showBinInfo() {
        return FurnitureNeoForgeConfig.showBinInfo;
    }

    public static boolean showTelescopeInfo() {
        return FurnitureNeoForgeConfig.showTelescopeInfo;
    }

    public static boolean showLampInfo() {
        return FurnitureNeoForgeConfig.showLampInfo;
    }

    public static boolean showCashRegisterInfo() {
        return FurnitureNeoForgeConfig.showCashRegisterInfo;
    }

    public static boolean showSteamVentInfo() {
        return FurnitureNeoForgeConfig.showSteamVentInfo;
    }

    public static boolean showBoatInAJarInfo() {
        return FurnitureNeoForgeConfig.showBoatInAJarInfo;
    }

    public static boolean showPellsInfo() {
        return FurnitureNeoForgeConfig.showPellsInfo;
    }

    public static boolean showPellsStats() {
        return FurnitureNeoForgeConfig.showPellsStats;
    }

    public static boolean areMirrorReflectionsEnabled() {
        return FurnitureNeoForgeConfig.enableReflections;
    }

    public static int getMirrorRenderDistance() {
        return FurnitureNeoForgeConfig.renderDistance;
    }

    public static int getMaxMirrors() {
        return FurnitureNeoForgeConfig.maxMirrors;
    }

    public static int getCashRegisterMerchantRange() {
        return FurnitureNeoForgeConfig.merchantRange;
    }

    public static int getCashRegisterAutoCloseSeconds() {
        return FurnitureNeoForgeConfig.autoCloseSeconds;
    }

    public static boolean isBoatInAJarShakingEnabled() {
        return FurnitureNeoForgeConfig.enableShaking;
    }

    public static boolean canBoatInAJarBreak() {
        return FurnitureNeoForgeConfig.enableBreaking;
    }

    public static int getPellsMaxDurability() {
        return FurnitureNeoForgeConfig.maxDurability;
    }

    public static int getPellsWheatRepair() {
        return FurnitureNeoForgeConfig.wheatRepair;
    }

    public static int getBinBagDropChance() {
        return FurnitureNeoForgeConfig.bagDropChance;
    }

    public static boolean isBinStinkEnabled() {
        return FurnitureNeoForgeConfig.enableStink;
    }

    public static boolean areBinFliesEnabled() {
        return FurnitureNeoForgeConfig.enableFlies;
    }

    public static boolean isTrashBagCooldownEnabled() {
        return FurnitureNeoForgeConfig.enableTrashBagCooldown;
    }

    public static int getTrashBagCooldownSeconds() {
        return FurnitureNeoForgeConfig.trashBagCooldownSeconds;
    }

    public static int getSteamVentSourceRange() {
        return FurnitureNeoForgeConfig.ventSourceRange;
    }

    public static int getSteamMaxPipeLength() {
        return FurnitureNeoForgeConfig.maxPipeLength;
    }

    public static boolean areShootingStarsEnabled() {
        return FurnitureNeoForgeConfig.enableShootingStars;
    }

    public static int getShootingStarChance() {
        return FurnitureNeoForgeConfig.shootingStarRarity;
    }

    public static boolean isDisplayRotationEnabled() {
        return FurnitureNeoForgeConfig.enableRotation;
    }

    public static boolean isDisplayBobbingEnabled() {
        return FurnitureNeoForgeConfig.enableBobbing;
    }

    public static double getTelescopeMinMagnification() {
        return FurnitureNeoForgeConfig.minMagnification;
    }

    public static double getTelescopeMaxMagnification() {
        return FurnitureNeoForgeConfig.maxMagnification;
    }

    public static int getTelescopeMaxDistance() {
        return FurnitureNeoForgeConfig.maxDistance;
    }

    public static int getBoatInAJarBreakThreshold() {
        return FurnitureNeoForgeConfig.breakThreshold;
    }

    public static int getBoatInAJarAnimationSpeed() {
        return FurnitureNeoForgeConfig.animationSpeed;
    }

    public static boolean isCompatBenchesEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_BENCHES.get();
    }

    public static boolean isCompatCabinetsEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_CABINETS.get();
    }

    public static boolean isCompatClocksEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_CLOCKS.get();
    }

    public static boolean isCompatGrandfatherClocksEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_GRANDFATHER_CLOCKS.get();
    }

    public static boolean isCompatMirrorsEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_MIRRORS.get();
    }

    public static boolean isCompatDeskChairsEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_DESK_CHAIRS.get();
    }

    public static boolean isCompatShuttersEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_SHUTTERS.get();
    }

    public static boolean isCompatDesksEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_DESKS.get();
    }

    public static boolean isCompatDressersEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_DRESSERS.get();
    }

    public static boolean isCompatWardrobesEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_WARDROBES.get();
    }

    public static boolean isCompatCurtainsEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_CURTAINS.get();
    }

    public static boolean isCompatPouffesEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_POUFFES.get();
    }

    public static boolean isCompatLampsEnabled() {
        return FurnitureNeoForgeConfig.COMPAT_LAMPS.get();
    }

    public static boolean isChimneySootEnabled() {
        return FurnitureNeoForgeConfig.enableSoot;
    }

    public static int getChimneySootChance() {
        return FurnitureNeoForgeConfig.sootRarity;
    }

    public static boolean isGrandfatherClockChimeEnabled() {
        return FurnitureNeoForgeConfig.enableChime;
    }

    public static boolean isClockTickingEnabled() {
        return FurnitureNeoForgeConfig.enableTicking;
    }

    public static int getClockTickingVolume() {
        return FurnitureNeoForgeConfig.tickingVolume;
    }

    public static boolean isFishTankAmbienceEnabled() {
        return FurnitureNeoForgeConfig.enableFishTankAmbience;
    }

    public static int getCofferVolume() {
        return FurnitureNeoForgeConfig.cofferVolume;
    }

    public static boolean areSteamPipeParticlesEnabled() {
        return FurnitureNeoForgeConfig.enablePipeParticles;
    }

    public static boolean areSteamPipeSoundsEnabled() {
        return FurnitureNeoForgeConfig.enablePipeSounds;
    }

    public static boolean areSteamVentParticlesEnabled() {
        return FurnitureNeoForgeConfig.enableVentParticles;
    }

    public static int getBinFlyFrequency() {
        return FurnitureNeoForgeConfig.flyFrequency;
    }

    public static int getBinFlyLifetime() {
        return FurnitureNeoForgeConfig.flyLifetime;
    }

    public static boolean isMirrorBlurEnabled() {
        return FurnitureNeoForgeConfig.enableBlur;
    }

    public static int getMirrorReflectionRange() {
        return FurnitureNeoForgeConfig.reflectionRange;
    }

    public static boolean isGrandfatherClockMidnightStrikeEnabled() {
        return FurnitureNeoForgeConfig.enableMidnightStrike;
    }

    public static boolean isGrandfatherClockStartleMobsEnabled() {
        return FurnitureNeoForgeConfig.enableStartleMobs;
    }

    public static boolean isMirrorCreeperPrankEnabled() {
        return FurnitureNeoForgeConfig.enableCreeperPrank;
    }

    public static boolean isBoatInAJarStormEnabled() {
        return FurnitureNeoForgeConfig.enableStorm;
    }

    public static boolean isBoatInAJarThunderRockingEnabled() {
        return FurnitureNeoForgeConfig.enableThunderRocking;
    }

    public static boolean isPouffeBounceEnabled() {
        return FurnitureNeoForgeConfig.enableBounce;
    }

    public static int getPouffeBounceStrength() {
        return FurnitureNeoForgeConfig.bounceStrength;
    }

    public static int getPouffeFallDamage() {
        return FurnitureNeoForgeConfig.fallDamage;
    }

    public static boolean isGramophoneEerieElevenEnabled() {
        return FurnitureNeoForgeConfig.enableEerieEleven;
    }

    public static boolean isGramophoneHornPulseEnabled() {
        return FurnitureNeoForgeConfig.enableHornPulse;
    }

    public static boolean isFishTankCatWatchingEnabled() {
        return FurnitureNeoForgeConfig.enableCatsWatchFish;
    }
}
