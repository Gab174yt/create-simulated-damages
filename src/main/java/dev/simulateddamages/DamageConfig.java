package dev.simulateddamages;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class DamageConfig {
    public static final ModConfigSpec.DoubleValue IMPACT_SENSITIVITY;
    public static final ModConfigSpec.DoubleValue IMPACT_SPEED_THRESHOLD;
    public static final ModConfigSpec.DoubleValue EXPLOSION_SPEED_THRESHOLD;
    public static final ModConfigSpec.IntValue IMPACT_MAX_RADIUS;
    public static final ModConfigSpec.DoubleValue FRAGILE_BLOCK_RESISTANCE;
    public static final ModConfigSpec.DoubleValue EXPLOSION_EJECTION_PERCENT;
    public static final ModConfigSpec.IntValue MAX_EXPLOSION_SUBLEVELS;
    public static final ModConfigSpec.DoubleValue FRICTION_SENSITIVITY;
    public static final ModConfigSpec.DoubleValue FRICTION_SPEED_THRESHOLD;
    public static final ModConfigSpec.IntValue IMPACT_COOLDOWN_TICKS;
    public static final ModConfigSpec.IntValue REPAIR_INTERVAL_SECONDS;
    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("impacts");
        IMPACT_SENSITIVITY = builder
                .comment("Scales damage from impacts. Lower values reduce damage.")
                .translation("config.create_simulated_damages.impact_sensitivity")
                .defineInRange("impact_sensitivity", 0.5, 0.1, 4.0);
        IMPACT_SPEED_THRESHOLD = builder
                .comment("Minimum collision speed in blocks per second for impact damage.")
                .translation("config.create_simulated_damages.impact_speed_threshold")
                .defineInRange("impact_speed_threshold", 4.0, 0.1, 30.0);
        IMPACT_MAX_RADIUS = builder
                .comment("Maximum radius in blocks affected by a high-speed impact.")
                .translation("config.create_simulated_damages.impact_max_radius")
                .defineInRange("impact_max_radius", 4, 0, 16);
        EXPLOSION_SPEED_THRESHOLD = builder
                .comment("Minimum collision speed in blocks per second for impact explosions.")
                .translation("config.create_simulated_damages.explosion_speed_threshold")
                .defineInRange("explosion_speed_threshold", 8.0, 0.1, 40.0);
        FRAGILE_BLOCK_RESISTANCE = builder
                .comment("Resistance multiplier for blocks in the impact_fragile block tag.")
                .translation("config.create_simulated_damages.fragile_block_resistance")
                .defineInRange("fragile_block_resistance", 0.15, 0.01, 1.0);
        EXPLOSION_EJECTION_PERCENT = builder
                .comment("Percentage of affected explosion blocks to assemble and eject as a Sable sub-level.")
                .translation("config.create_simulated_damages.explosion_ejection_percent")
                .defineInRange("explosion_ejection_percent", 50.0, 0.0, 100.0);
        MAX_EXPLOSION_SUBLEVELS = builder
                .comment("Maximum number of Sable sub-levels created by one explosion; excess blocks are grouped.")
                .translation("config.create_simulated_damages.max_explosion_sublevels")
                .defineInRange("max_explosion_sublevels", 16, 1, 64);
        IMPACT_COOLDOWN_TICKS = builder
                .comment("Minimum ticks between impact damage events on the same block.")
                .translation("config.create_simulated_damages.impact_cooldown_ticks")
                .defineInRange("impact_cooldown_ticks", 10, 0, 200);
        builder.pop();

        builder.push("friction");
        FRICTION_SENSITIVITY = builder
                .comment("Scales friction wear. Set to 0 to disable friction damage.")
                .translation("config.create_simulated_damages.friction_sensitivity")
                .defineInRange("friction_sensitivity", 3.0, 0.0, 20.0);
        FRICTION_SPEED_THRESHOLD = builder
                .comment("Minimum relative contact speed before friction causes wear.")
                .translation("config.create_simulated_damages.friction_speed_threshold")
                .defineInRange("friction_speed_threshold", 0.05, 0.01, 5.0);
        builder.pop();

        builder.push("repair");
        REPAIR_INTERVAL_SECONDS = builder
                .comment("Seconds required to repair one crack level. Set to 0 to disable repair.")
                .translation("config.create_simulated_damages.repair_interval_seconds")
                .defineInRange("repair_interval_seconds", 10, 0, 3600);
        builder.pop();

        SPEC = builder.build();
    }

    private DamageConfig() {
    }

    public static double getImpactSensitivity() {
        return IMPACT_SENSITIVITY.get();
    }

    public static double getImpactSpeedThreshold() {
        return IMPACT_SPEED_THRESHOLD.get();
    }

    public static double getExplosionSpeedThreshold() {
        return EXPLOSION_SPEED_THRESHOLD.get();
    }

    public static int getImpactMaxRadius() {
        return IMPACT_MAX_RADIUS.get();
    }

    public static double getFragileBlockResistance() {
        return FRAGILE_BLOCK_RESISTANCE.get();
    }

    public static double getExplosionEjectionPercent() {
        return EXPLOSION_EJECTION_PERCENT.get();
    }

    public static int getMaxExplosionSublevels() {
        return MAX_EXPLOSION_SUBLEVELS.get();
    }

    public static double getFrictionSensitivity() {
        return FRICTION_SENSITIVITY.get();
    }

    public static double getFrictionSpeedThreshold() {
        return FRICTION_SPEED_THRESHOLD.get();
    }

    public static int getRepairIntervalTicks() {
        return REPAIR_INTERVAL_SECONDS.get() * 20;
    }
}
