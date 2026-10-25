package dev.simulateddamages;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
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

    private static final float IMPACT_EXPLOSION_SPEED = 8.0f;
    private static final double MINIMUM_IMPACT_SPEED = 4.0;
    private static final double IMPACT_DAMAGE_SCALE = 0.15;
    private static final long IMPACT_COOLDOWN_TICKS = 10;
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

        if (state.is(IMPACT_EXPLOSIVES) && Math.abs(impactSpeed) >= IMPACT_EXPLOSION_SPEED) {
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
        damageImpact(level, position, speed);
    }

    public static void damageImpact(ServerLevel level, BlockPos position, double impactVelocity) {
        double speed = Math.abs(impactVelocity);
        if (speed <= MINIMUM_IMPACT_SPEED || !registerImpact(level, position)) {
            return;
        }
        float damage = (float) (Math.pow(speed - MINIMUM_IMPACT_SPEED, 2) * IMPACT_DAMAGE_SCALE);
        damageBlock(level, position, damage, impactVelocity);
    }

    public static void damageFriction(ServerLevel level, BlockPos position, double speed) {
        if (speed < 0.05) {
            return;
        }
        damageBlock(level, position, (float) Math.max(0.01, speed * 0.015), 0);
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
        if (lastImpact != null && gameTime - lastImpact < IMPACT_COOLDOWN_TICKS) {
            return false;
        }

        positionTicks.put(packedPos, gameTime);
        if (Math.floorMod(gameTime, 200) == 0) {
            positionTicks.entrySet().removeIf(entry -> gameTime - entry.getValue() >= IMPACT_COOLDOWN_TICKS);
        }
        return true;
    }

    private static int breakerId(BlockPos pos) {
        return Integer.MIN_VALUE + (pos.hashCode() & 0x0FFFFFFF);
    }
}
