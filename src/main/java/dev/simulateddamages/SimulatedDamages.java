package dev.simulateddamages;

import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

@Mod(SimulatedDamages.MOD_ID)
public final class SimulatedDamages {
    public static final String MOD_ID = "create_simulated_damages";

    public SimulatedDamages(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, DamageConfig.SPEC);
    }
}
