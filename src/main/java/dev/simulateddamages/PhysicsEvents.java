package dev.simulateddamages;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = SimulatedDamages.MOD_ID)
public final class PhysicsEvents {
    private static final Map<Entity, Vec3> MOTION_BEFORE_TICK = new WeakHashMap<>();

    private PhysicsEvents() {
    }

    @SubscribeEvent
    public static void captureMotion(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        if (entity.level() instanceof ServerLevel) {
            MOTION_BEFORE_TICK.put(entity, entity.getDeltaMovement());
        }
    }

    @SubscribeEvent
    public static void processMotion(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || entity.isRemoved()
                || entity instanceof FallingBlockEntity) {
            MOTION_BEFORE_TICK.remove(entity);
            return;
        }

        Vec3 motion = MOTION_BEFORE_TICK.remove(entity);
        if (motion == null) {
            return;
        }

        double speed = motion.length();
        if (entity.horizontalCollision || entity.verticalCollision) {
            BlockPos target = collisionTarget(entity, motion);
            DamageEngine.damageEntityImpact(level, target, speed);
        }

        if (entity.onGround() && motion.horizontalDistanceSqr() > 0.0001
                && Math.floorMod(level.getGameTime(), 10) == Math.floorMod(entity.getId(), 10)) {
            DamageEngine.damageFriction(level, entity.blockPosition().below(), motion.horizontalDistance());
        }
    }

    @SubscribeEvent
    public static void processDeferredMutations(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            DamageEngine.processDeferredMutations(level);
        }
    }

    @SubscribeEvent
    public static void ejectExplosionBlocks(ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        event.getAffectedBlocks().forEach(pos -> DamageEngine.clearDamage(level, pos));
        var iterator = event.getAffectedBlocks().iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            var state = level.getBlockState(pos);
            if (!state.is(DamageEngine.EXPLOSION_EJECTABLE) || level.getBlockEntity(pos) != null) {
                continue;
            }

            iterator.remove();
            level.removeBlock(pos, false);
            FallingBlockEntity debris = FallingBlockEntity.fall(level, pos, state);
            Vec3 kick = new Vec3(
                    level.random.nextDouble() - 0.5,
                    0.25 + level.random.nextDouble() * 0.5,
                    level.random.nextDouble() - 0.5
            ).normalize().scale(0.45 + level.random.nextDouble() * 0.8);
            debris.setDeltaMovement(kick);
        }
    }

    @SubscribeEvent
    public static void clearBrokenBlockDamage(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            DamageEngine.clearDamage(level, event.getPos());
        }
    }

    private static BlockPos collisionTarget(Entity entity, Vec3 motion) {
        if (entity.horizontalCollision) {
            double horizontalLength = Math.max(0.001, motion.horizontalDistance());
            double offset = entity.getBbWidth() * 0.5 + 0.1;
            return BlockPos.containing(
                    entity.getX() + motion.x / horizontalLength * offset,
                    entity.getY() + entity.getBbHeight() * 0.5,
                    entity.getZ() + motion.z / horizontalLength * offset
            );
        }

        double y = motion.y < 0 ? entity.getY() - 0.1 : entity.getY() + entity.getBbHeight() + 0.1;
        return BlockPos.containing(entity.getX(), y, entity.getZ());
    }
}
