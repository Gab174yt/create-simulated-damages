package dev.simulateddamages;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import dev.ryanhcode.sable.api.physics.callback.BlockSubLevelCollisionCallback;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public final class BlockImpactCallback implements BlockSubLevelCollisionCallback {
    public static final BlockImpactCallback INSTANCE = new BlockImpactCallback();

    private static final double BASE_MINIMUM_IMPACT_SPEED = 4.0;
    private static final Map<ServerLevel, FrictionContacts> FRICTION_CONTACTS = new WeakHashMap<>();

    private BlockImpactCallback() {
    }

    @Override
    public CollisionResult sable$onCollision(BlockPos hitBlockPos, @Nullable BlockPos otherHitBlockPos,
                                             Vector3d impactPosition, double impactVelocity) {
        double speed = Math.abs(impactVelocity);
        if (speed < 0.05) {
            return CollisionResult.NONE;
        }

        SubLevelPhysicsSystem physicsSystem = SubLevelPhysicsSystem.getCurrentlySteppingSystem();
        if (physicsSystem == null) {
            return CollisionResult.NONE;
        }

        ServerLevel level = physicsSystem.getLevel();
        if (speed < DamageEngine.getImpactThreshold(BASE_MINIMUM_IMPACT_SPEED)) {
            if (markFrictionContact(level, hitBlockPos)) {
                DamageEngine.damageFriction(level, hitBlockPos, speed);
            }
            if (otherHitBlockPos != null && markFrictionContact(level, otherHitBlockPos)) {
                DamageEngine.damageFriction(level, otherHitBlockPos, speed);
            }
            return CollisionResult.NONE;
        }

        DamageEngine.damageImpact(level, hitBlockPos, impactVelocity);
        if (otherHitBlockPos != null) {
            DamageEngine.damageImpact(level, otherHitBlockPos, impactVelocity);
        }

        return CollisionResult.NONE;
    }

    private static synchronized boolean markFrictionContact(ServerLevel level, BlockPos position) {
        long gameTime = level.getGameTime();
        FrictionContacts contacts = FRICTION_CONTACTS.computeIfAbsent(level, ignored -> new FrictionContacts());
        if (contacts.gameTime != gameTime) {
            contacts.gameTime = gameTime;
            contacts.positions.clear();
        }
        return contacts.positions.add(position.asLong());
    }

    private static final class FrictionContacts {
        private long gameTime = Long.MIN_VALUE;
        private final Set<Long> positions = new HashSet<>();
    }
}
