package net.satisfy.furniture.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.platform.Platform;

public abstract class PlatformHelper {
    public static boolean isCompatLoaded(String modId) {
        return Platform.isModLoaded(modId) && isCompatEnabled(modId);
    }

    public static boolean isCompatAllowed(String modId, String type) {
        if (!isCompatLoaded(modId)) {
            return false;
        }
        return switch (type) {
            case "benches" -> isCompatBenchesEnabled();
            case "cabinets" -> isCompatCabinetsEnabled();
            case "clocks" -> isCompatClocksEnabled();
            case "grandfather_clocks" -> isCompatGrandfatherClocksEnabled();
            case "mirrors" -> isCompatMirrorsEnabled();
            case "desk_chairs" -> isCompatDeskChairsEnabled();
            case "shutters" -> isCompatShuttersEnabled();
            case "desks" -> isCompatDesksEnabled();
            case "dressers" -> isCompatDressersEnabled();
            case "wardrobes" -> isCompatWardrobesEnabled();
            case "curtains" -> isCompatCurtainsEnabled();
            case "pouffes" -> isCompatPouffesEnabled();
            case "lamps" -> isCompatLampsEnabled();
            default -> true;
        };
    }

    @ExpectPlatform
    public static boolean isCompatEnabled(String modId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showFishTankInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showWardrobeInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showBinInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showTelescopeInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showLampInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showCashRegisterInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showSteamVentInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showBoatInAJarInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showPellsInfo() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean showPellsStats() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean areMirrorReflectionsEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getMirrorRenderDistance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getMaxMirrors() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getCashRegisterMerchantRange() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getCashRegisterAutoCloseSeconds() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isBoatInAJarShakingEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean canBoatInAJarBreak() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getPellsMaxDurability() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getPellsWheatRepair() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getBinBagDropChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isBinStinkEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean areBinFliesEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isTrashBagCooldownEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getTrashBagCooldownSeconds() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getSteamVentSourceRange() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getSteamMaxPipeLength() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean areShootingStarsEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getShootingStarChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDisplayRotationEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isDisplayBobbingEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static double getTelescopeMinMagnification() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static double getTelescopeMaxMagnification() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getTelescopeMaxDistance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getBoatInAJarBreakThreshold() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getBoatInAJarAnimationSpeed() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatBenchesEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatCabinetsEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatClocksEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatGrandfatherClocksEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatMirrorsEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatDeskChairsEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatShuttersEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatDesksEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatDressersEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatWardrobesEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatCurtainsEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatPouffesEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isCompatLampsEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isChimneySootEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getChimneySootChance() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isGrandfatherClockChimeEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isClockTickingEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getClockTickingVolume() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isFishTankAmbienceEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getCofferVolume() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean areSteamPipeParticlesEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean areSteamPipeSoundsEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean areSteamVentParticlesEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getBinFlyFrequency() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getBinFlyLifetime() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isMirrorBlurEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getMirrorReflectionRange() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isGrandfatherClockMidnightStrikeEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isGrandfatherClockStartleMobsEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isMirrorCreeperPrankEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isBoatInAJarStormEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isBoatInAJarThunderRockingEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isPouffeBounceEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getPouffeBounceStrength() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int getPouffeFallDamage() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isGramophoneEerieElevenEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isGramophoneHornPulseEnabled() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isFishTankCatWatchingEnabled() {
        throw new AssertionError();
    }
}
