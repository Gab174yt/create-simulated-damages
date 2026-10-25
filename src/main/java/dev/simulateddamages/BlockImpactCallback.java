package dev.simulateddamages;

import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public final class BlockImpactCallback implements BlockSubLevelCollisionCallback {
    public static final BlockImpactCallback INSTANCE = new BlockImpactCallback();

    private static final double MINIMUM_IMPACT_SPEED = 1.5;
    private static final double IMPACT_DAMAGE_SCALE = 0.12;

    private BlockImpactCallback() {
    }

    @Override
    public CollisionResult sable$onCollision(BlockPos hitBlockPos, @Nullable BlockPos otherHitBlockPos,
                                             Vector3d impactPosition, double impactVelocity) {
        double speed = Math.abs(impactVelocity);
        if (speed < MINIMUM_IMPACT_SPEED) {
            return CollisionResult.NONE;
        }

        SubLevelPhysicsSystem physicsSystem = SubLevelPhysicsSystem.getCurrentlySteppingSystem();
        if (physicsSystem == null) {
            return CollisionResult.NONE;
        }

        ServerLevel level = physicsSystem.getLevel();
        float damage = (float) (speed * speed * IMPACT_DAMAGE_SCALE);
        DamageEngine.damageBlock(level, hitBlockPos, damage, impactVelocity);
        if (otherHitBlockPos != null) {
            DamageEngine.damageBlock(level, otherHitBlockPos, damage, impactVelocity);
        }

        return CollisionResult.NONE;
    }
}
