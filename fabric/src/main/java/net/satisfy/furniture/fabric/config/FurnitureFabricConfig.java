package net.satisfy.furniture.fabric.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "furniture")
@Config.Gui.Background("minecraft:textures/block/oak_planks.png")
public class FurnitureFabricConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public CompatSettings compat = new CompatSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public MirrorSettings mirror = new MirrorSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public CashRegisterSettings cashRegister = new CashRegisterSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public BoatInAJarSettings boatInAJar = new BoatInAJarSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public PouffeSettings pouffe = new PouffeSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public GramophoneSettings gramophone = new GramophoneSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public FishTankSettings fishTank = new FishTankSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public PellsSettings pells = new PellsSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public BinSettings bin = new BinSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public SteamSettings steam = new SteamSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public TelescopeSettings telescope = new TelescopeSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public DisplaySettings display = new DisplaySettings();

    @ConfigEntry.Gui.CollapsibleObject
    public ChimneySettings chimney = new ChimneySettings();

    @ConfigEntry.Gui.CollapsibleObject
    public ClockSettings clock = new ClockSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public InfoTooltipSettings infoTooltips = new InfoTooltipSettings();

    @ConfigEntry.Gui.CollapsibleObject
    public MiscSettings misc = new MiscSettings();

    public static class CompatSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean enableCompat = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean bloomingNature = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean meadow = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean beachparty = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean alpineWhispers = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean vinery = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean benches = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean cabinets = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean clocks = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean grandfatherClocks = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean mirrors = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean deskChairs = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean shutters = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean desks = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean dressers = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean wardrobes = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean curtains = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean pouffes = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.Gui.RequiresRestart
        public boolean lamps = true;
    }

    public static class MirrorSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableReflections = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 4, max = 64)
        public int renderDistance = 24;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 8)
        public int maxMirrors = 4;

        @ConfigEntry.Gui.Tooltip
        public boolean enableBlur = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 4, max = 64)
        public int reflectionRange = 24;

        @ConfigEntry.Gui.Tooltip
        public boolean enableCreeperPrank = true;
    }

    public static class CashRegisterSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 4, max = 32)
        public int merchantRange = 16;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 120)
        public int autoCloseSeconds = 10;
    }

    public static class BoatInAJarSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableShaking = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableBreaking = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 2, max = 20)
        public int breakThreshold = 5;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 500)
        public int animationSpeed = 100;

        @ConfigEntry.Gui.Tooltip
        public boolean enableStorm = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableThunderRocking = true;
    }

    public static class PouffeSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableBounce = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int bounceStrength = 66;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int fallDamage = 50;
    }

    public static class GramophoneSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableEerieEleven = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableHornPulse = true;
    }

    public static class FishTankSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableCatsWatchFish = true;
    }

    public static class PellsSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 10, max = 1000)
        public int maxDurability = 150;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 150)
        public int wheatRepair = 15;
    }

    public static class BinSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 100)
        public int bagDropChance = 25;

        @ConfigEntry.Gui.Tooltip
        public boolean enableStink = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableFlies = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableTrashBagCooldown = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 600)
        public int trashBagCooldownSeconds = 60;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 200)
        public int flyFrequency = 12;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 10, max = 400)
        public int flyLifetime = 60;
    }

    public static class SteamSettings {
        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 1, max = 16)
        public int ventSourceRange = 4;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 16, max = 512)
        public int maxPipeLength = 128;

        @ConfigEntry.Gui.Tooltip
        public boolean enablePipeParticles = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enablePipeSounds = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableVentParticles = true;
    }

    public static class TelescopeSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableShootingStars = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 10, max = 10000)
        public int shootingStarRarity = 600;

        @ConfigEntry.Gui.Tooltip
        public double minMagnification = 2.5;

        @ConfigEntry.Gui.Tooltip
        public double maxMagnification = 50.0;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 2, max = 16)
        public int maxDistance = 3;
    }

    public static class DisplaySettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableRotation = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableBobbing = true;
    }

    public static class ChimneySettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableSoot = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 100, max = 100000)
        public int sootRarity = 12000;
    }

    public static class ClockSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableChime = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableMidnightStrike = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableStartleMobs = true;

        @ConfigEntry.Gui.Tooltip
        public boolean enableTicking = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 200)
        public int tickingVolume = 100;
    }

    public static class InfoTooltipSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean showFishTankInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showWardrobeInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showBinInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showTelescopeInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showLampInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showCashRegisterInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showSteamVentInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showBoatInAJarInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showPellsInfo = true;

        @ConfigEntry.Gui.Tooltip
        public boolean showPellsStats = true;
    }

    public static class MiscSettings {
        @ConfigEntry.Gui.Tooltip
        public boolean enableFishTankAmbience = true;

        @ConfigEntry.Gui.Tooltip
        @ConfigEntry.BoundedDiscrete(min = 0, max = 200)
        public int cofferVolume = 100;
    }

}
