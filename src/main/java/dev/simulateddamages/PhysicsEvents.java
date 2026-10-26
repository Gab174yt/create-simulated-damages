package dev.simulateddamages;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.joml.Vector3d;

@EventBusSubscriber(modid = SimulatedDamages.MOD_ID)
public final class PhysicsEvents {
    private static final Map<Entity, Vec3> MOTION_BEFORE_TICK = new WeakHashMap<>();
    private static final double EXPLOSION_DEBRIS_HORIZONTAL_SPEED = 4.0;
    private static final double EXPLOSION_DEBRIS_UPWARD_SPEED = 8.0;

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
        if (!(entity.level() instanceof ServerLevel level) || entity.isRemoved()) {
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
            DamageEngine.damageEntityImpact(level, target, speed * 20.0);
        }

        if (entity.onGround() && motion.horizontalDistanceSqr() > 0.0001
                && Math.floorMod(level.getGameTime(), 10) == Math.floorMod(entity.getId(), 10)) {
            DamageEngine.damageFriction(level, entity.blockPosition().below(),
                    motion.horizontalDistance() * 20.0);
        }
    }

    @SubscribeEvent
    public static void processDeferredMutations(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            DamageEngine.processDeferredMutations(level);
            DamageEngine.repairDamagedBlocks(level);
        }
    }

    @SubscribeEvent
    public static void ejectExplosionBlocks(ExplosionEvent.Detonate event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        List<BlockPos> candidates = new ArrayList<>();
        for (BlockPos pos : event.getAffectedBlocks()) {
            if (level.isInWorldBounds(pos) && !level.getBlockState(pos).isAir()
                    && level.getBlockEntity(pos) == null) {
                candidates.add(pos.immutable());
            }
        }
        if (candidates.isEmpty() || DamageConfig.getExplosionEjectionPercent() <= 0) {
            return;
        }

        shuffle(candidates, level);
        double desiredCount = candidates.size() * DamageConfig.getExplosionEjectionPercent() / 100.0;
        int ejectCount = (int) Math.floor(desiredCount);
        if (desiredCount - ejectCount > 0 && level.random.nextDouble() < desiredCount - ejectCount) {
            ejectCount++;
        }
        if (ejectCount == 0) {
            return;
        }
        List<BlockPos> ejected = List.copyOf(candidates.subList(0, ejectCount));
        BlockPos anchor = BlockPos.containing(event.getExplosion().center());
        SubLevel containing = Sable.HELPER.getContaining(level, anchor);
        if (containing != null && containing.isRemoved()) {
            return;
        }

        // Remove these positions before Minecraft processes the remaining explosion blocks.
        event.getAffectedBlocks().removeAll(ejected);
        ejected.forEach(pos -> DamageEngine.clearDamage(level, pos));
        Vec3 center = event.getExplosion().center();
        for (List<BlockPos> group : groupDebris(ejected, DamageConfig.getMaxExplosionSublevels(), center)) {
            BoundingBox3i bounds = BoundingBox3i.from(group);
            ServerSubLevel debris = SubLevelAssemblyHelper.assembleBlocks(level, anchor, group, bounds);
            Vec3 radial = bounds.toAABB().getCenter().subtract(center);
            Vec3 horizontal = new Vec3(radial.x, 0, radial.z);
            if (horizontal.lengthSqr() < 1.0E-6) {
                horizontal = new Vec3(level.random.nextDouble() - 0.5, 0,
                        level.random.nextDouble() - 0.5);
            }
            Vec3 horizontalVelocity = horizontal.normalize().scale(EXPLOSION_DEBRIS_HORIZONTAL_SPEED);
            Vector3d velocity = new Vector3d(horizontalVelocity.x, EXPLOSION_DEBRIS_UPWARD_SPEED,
                    horizontalVelocity.z);
            RigidBodyHandle.of(debris).addLinearAndAngularVelocity(velocity, new Vector3d());
        }
    }

    private static List<List<BlockPos>> groupDebris(List<BlockPos> blocks, int maxGroups, Vec3 origin) {
        int groupCount = Math.min(blocks.size(), maxGroups);
        if (groupCount == blocks.size()) {
            return blocks.stream().map(List::of).toList();
        }

        List<BlockPos> centers = new ArrayList<>(groupCount);
        Set<BlockPos> selectedCenters = new HashSet<>();
        BlockPos firstCenter = blocks.stream()
                .max((first, second) -> Double.compare(distanceSquared(first, origin),
                        distanceSquared(second, origin)))
                .orElseThrow();
        centers.add(firstCenter);
        selectedCenters.add(firstCenter);

        while (centers.size() < groupCount) {
            BlockPos nextCenter = null;
            double greatestDistance = -1;
            for (BlockPos pos : blocks) {
                if (selectedCenters.contains(pos)) {
                    continue;
                }
                double nearestDistance = Double.POSITIVE_INFINITY;
                for (BlockPos center : centers) {
                    nearestDistance = Math.min(nearestDistance, distanceSquared(pos, center));
                }
                if (nearestDistance > greatestDistance) {
                    greatestDistance = nearestDistance;
                    nextCenter = pos;
                }
            }
            centers.add(nextCenter);
            selectedCenters.add(nextCenter);
        }

        List<List<BlockPos>> groups = new ArrayList<>(groupCount);
        for (int index = 0; index < groupCount; index++) {
            groups.add(new ArrayList<>());
        }
        for (BlockPos pos : blocks) {
            int nearestIndex = 0;
            double nearestDistance = Double.POSITIVE_INFINITY;
            for (int index = 0; index < centers.size(); index++) {
                double distance = distanceSquared(pos, centers.get(index));
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestIndex = index;
                }
            }
            groups.get(nearestIndex).add(pos);
        }
        return groups;
    }

    private static double distanceSquared(BlockPos pos, Vec3 point) {
        double dx = pos.getX() + 0.5 - point.x;
        double dy = pos.getY() + 0.5 - point.y;
        double dz = pos.getZ() + 0.5 - point.z;
        return dx * dx + dy * dy + dz * dz;
    }

    private static double distanceSquared(BlockPos first, BlockPos second) {
        double dx = first.getX() - second.getX();
        double dy = first.getY() - second.getY();
        double dz = first.getZ() - second.getZ();
        return dx * dx + dy * dy + dz * dz;
    }

    private static void shuffle(List<BlockPos> positions, ServerLevel level) {
        for (int index = positions.size() - 1; index > 0; index--) {
            int otherIndex = level.random.nextInt(index + 1);
            BlockPos current = positions.get(index);
            positions.set(index, positions.get(otherIndex));
            positions.set(otherIndex, current);
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
