package dev.simulateddamages;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class DamageConfig {
    public static final ModConfigSpec.DoubleValue IMPACT_SENSITIVITY;
    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        IMPACT_SENSITIVITY = builder
                .comment("Scales impact sensitivity. 0.5 is less sensitive; 2.0 is more sensitive.")
                .translation("config.create_simulated_damages.impact_sensitivity")
                .defineInRange("impact_sensitivity", 1.0, 0.25, 4.0);
        SPEC = builder.build();
    }

    private DamageConfig() {
    }

    public static double getImpactSensitivity() {
        return IMPACT_SENSITIVITY.get();
    }
}
