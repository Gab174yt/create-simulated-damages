package dev.simulateddamages;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class DamageConfig {
    public static final ModConfigSpec.DoubleValue IMPACT_SENSITIVITY;
    public static final ModConfigSpec.DoubleValue IMPACT_SPEED_THRESHOLD;
    public static final ModConfigSpec.DoubleValue EXPLOSION_SPEED_THRESHOLD;
    public static final ModConfigSpec.DoubleValue FRICTION_SENSITIVITY;
    public static final ModConfigSpec.DoubleValue FRICTION_SPEED_THRESHOLD;
    public static final ModConfigSpec.IntValue IMPACT_COOLDOWN_TICKS;
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
        EXPLOSION_SPEED_THRESHOLD = builder
                .comment("Minimum collision speed in blocks per second for impact explosions.")
                .translation("config.create_simulated_damages.explosion_speed_threshold")
                .defineInRange("explosion_speed_threshold", 8.0, 0.1, 40.0);
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

    public static double getFrictionSensitivity() {
        return FRICTION_SENSITIVITY.get();
    }

    public static double getFrictionSpeedThreshold() {
        return FRICTION_SPEED_THRESHOLD.get();
    }
}
