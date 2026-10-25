package dev.simulateddamages;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class DamageEngine {
    public static final TagKey<Block> DISLOCATABLE_BLOCKS = TagKey.create(
            Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SimulatedDamages.MOD_ID, "dislocatable"));
    public static final TagKey<Block> IMPACT_EXPLOSIVES = TagKey.create(
            Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SimulatedDamages.MOD_ID, "impact_explosives"));
    public static final TagKey<Block> EXPLOSION_EJECTABLE = TagKey.create(
            Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(SimulatedDamages.MOD_ID, "explosion_ejectable"));

    private static final double IMPACT_DAMAGE_SCALE = 0.04;
    private static final double FRICTION_DAMAGE_SCALE = 0.02;
    private static final String DAMAGE_DATA_NAME = "create_simulated_damages_v2";
    private static final Map<ServerLevel, Map<Long, Long>> LAST_IMPACT_TICKS = new WeakHashMap<>();

    private DamageEngine() {
    }

    public static void damageBlock(ServerLevel level, BlockPos position, float amount, double impactSpeed) {
        BlockPos pos = position.immutable();
        if (!level.isInWorldBounds(pos)) {
            return;
        }

        BlockState state = level.getBlockState(pos);
        if (state.isAir() || level.getBlockEntity(pos) != null) {
            return;
        }

        if (state.is(IMPACT_EXPLOSIVES)
                && Math.abs(impactSpeed) >= DamageConfig.getExplosionSpeedThreshold()) {
            level.getServer().execute(() -> explodeBlock(level, pos, state, (float) Math.abs(impactSpeed)));
            return;
        }

        float hardness = state.getDestroySpeed(level, pos);
        if (hardness < 0) {
            return;
        }

        level.getServer().execute(() -> applyDamage(level, pos, state, amount, hardness));
    }

    public static void damageEntityImpact(ServerLevel level, BlockPos position, double speed) {
        damageImpact(level, position, speed, null);
    }

    public static void damageImpact(ServerLevel level, BlockPos position, double impactVelocity,
                                    BlockPos otherPosition) {
        double speed = Math.abs(impactVelocity);
        double impactThreshold = DamageConfig.getImpactSpeedThreshold();
        double sensitivity = DamageConfig.getImpactSensitivity();
        if (speed < impactThreshold || !registerImpact(level, position)) {
            return;
        }
        double massScale = impactMassScale(level, position, otherPosition);
        float damage = (float) (Math.pow(speed - impactThreshold, 2)
                * IMPACT_DAMAGE_SCALE * sensitivity * massScale);
        damageBlock(level, position, damage, impactVelocity);
    }

    public static void damageFriction(ServerLevel level, BlockPos position, double speed) {
        damageFriction(level, position, speed, null);
    }

    public static void damageFriction(ServerLevel level, BlockPos position, double speed,
                                      BlockPos otherPosition) {
        if (speed < 0.01 || DamageConfig.getFrictionSensitivity() <= 0) {
            return;
        }

        BlockPos pos = position.immutable();
        if (!level.isInWorldBounds(pos)) {
            return;
        }
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return;
        }

        double friction = PhysicsBlockPropertyHelper.getFriction(state);
        if (otherPosition != null) {
            BlockState otherState = level.getBlockState(otherPosition);
            if (!otherState.isAir()) {
                friction = Math.sqrt(friction * PhysicsBlockPropertyHelper.getFriction(otherState));
            }
        }
        if (!Double.isFinite(friction) || friction <= 0) {
            return;
        }

        double massScale = impactMassScale(level, pos, otherPosition);
        float damage = (float) (speed * FRICTION_DAMAGE_SCALE
                * DamageConfig.getFrictionSensitivity() * friction * massScale);
        damageBlock(level, pos, damage, 0);
    }

    public static void clearDamage(ServerLevel level, BlockPos position) {
        BlockDamageData data = level.getDataStorage().get(BlockDamageData.FACTORY, DAMAGE_DATA_NAME);
        if (data != null && data.clear(position)) {
            level.destroyBlockProgress(breakerId(position), position, -1);
        }
    }

    private static void applyDamage(ServerLevel level, BlockPos pos, BlockState expectedState,
                                    float amount, float hardness) {
        if (!level.getBlockState(pos).equals(expectedState) || level.getBlockEntity(pos) != null) {
            clearDamage(level, pos);
            return;
        }

        float threshold = Math.max(1.0f, (hardness + 0.5f) * 2.0f);
        BlockDamageData data = getData(level);
        float totalDamage = data.add(pos, expectedState, amount);
        if (totalDamage >= threshold) {
            data.clear(pos);
            level.destroyBlockProgress(breakerId(pos), pos, -1);
            if (expectedState.is(DISLOCATABLE_BLOCKS)) {
                dislocate(level, pos, expectedState, amount);
            } else {
                level.destroyBlock(pos, true);
            }
            return;
        }

        int stage = Math.min(9, (int) (totalDamage / threshold * 10));
        level.destroyBlockProgress(breakerId(pos), pos, stage);
    }

    private static void explodeBlock(ServerLevel level, BlockPos pos, BlockState expectedState, float speed) {
        if (!level.getBlockState(pos).equals(expectedState)) {
            return;
        }

        clearDamage(level, pos);
        level.removeBlock(pos, false);
        float power = Math.min(8.0f, Math.max(3.0f, speed * 0.6f));
        level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                power, Level.ExplosionInteraction.TNT);
    }

    private static void dislocate(ServerLevel level, BlockPos pos, BlockState state, float impact) {
        level.removeBlock(pos, false);
        FallingBlockEntity debris = FallingBlockEntity.fall(level, pos, state);
        Vec3 kick = new Vec3(
                level.random.nextDouble() - 0.5,
                0.2 + level.random.nextDouble() * 0.45,
                level.random.nextDouble() - 0.5
        ).normalize().scale(Math.min(1.5, 0.2 + impact * 0.15));
        debris.setDeltaMovement(kick);
        debris.setHurtsEntities(1.0f, 20);
    }

    private static BlockDamageData getData(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(BlockDamageData.FACTORY, DAMAGE_DATA_NAME);
    }

    private static synchronized boolean registerImpact(ServerLevel level, BlockPos position) {
        long gameTime = level.getGameTime();
        Map<Long, Long> positionTicks = LAST_IMPACT_TICKS.computeIfAbsent(level, ignored -> new HashMap<>());
        long packedPos = position.asLong();
        Long lastImpact = positionTicks.get(packedPos);
        long cooldown = DamageConfig.IMPACT_COOLDOWN_TICKS.get();
        if (lastImpact != null && gameTime - lastImpact < cooldown) {
            return false;
        }

        positionTicks.put(packedPos, gameTime);
        if (Math.floorMod(gameTime, 200) == 0) {
            positionTicks.entrySet().removeIf(entry -> gameTime - entry.getValue() >= cooldown);
        }
        return true;
    }

    private static double impactMassScale(ServerLevel level, BlockPos position, BlockPos otherPosition) {
        double mass = blockMass(level, position);
        double otherMass = otherPosition == null ? mass : blockMass(level, otherPosition);
        double effectiveMass = mass * otherMass / (mass + otherMass);
        return Math.max(0.25, Math.min(4.0, effectiveMass / 0.5));
    }

    private static double blockMass(ServerLevel level, BlockPos position) {
        if (!level.isInWorldBounds(position)) {
            return 1.0;
        }
        BlockState state = level.getBlockState(position);
        if (state.isAir()) {
            return 1.0;
        }

        double mass = PhysicsBlockPropertyHelper.getMass(level, position, state);
        return Double.isFinite(mass) && mass > 0 ? mass : 1.0;
    }

    private static int breakerId(BlockPos pos) {
        return Integer.MIN_VALUE + (pos.hashCode() & 0x0FFFFFFF);
    }
}
