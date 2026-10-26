package dev.simulateddamages;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

public final class BlockDamageData extends SavedData {
    public static final Factory<BlockDamageData> FACTORY =
            new Factory<>(BlockDamageData::new, BlockDamageData::load, null);

    private static final String DAMAGE_LIST = "damages";
    private final Map<Long, Entry> damageByPosition = new HashMap<>();

    private BlockDamageData() {
    }

    private static BlockDamageData load(CompoundTag tag, HolderLookup.Provider registries) {
        BlockDamageData data = new BlockDamageData();
        ListTag entries = tag.getList(DAMAGE_LIST, Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            data.damageByPosition.put(entry.getLong("pos"),
                    new Entry(entry.getString("block"), entry.getFloat("progress"),
                            entry.getLong("lastRepairTick")));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag entries = new ListTag();
        damageByPosition.forEach((pos, entry) -> {
            CompoundTag savedEntry = new CompoundTag();
            savedEntry.putLong("pos", pos);
            savedEntry.putString("block", entry.blockId);
            savedEntry.putFloat("progress", entry.progress);
            savedEntry.putLong("lastRepairTick", entry.lastRepairTick);
            entries.add(savedEntry);
        });
        tag.put(DAMAGE_LIST, entries);
        return tag;
    }

    public float add(BlockPos pos, BlockState state, float amount, long gameTime) {
        long key = pos.asLong();
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        Entry entry = damageByPosition.get(key);
        float total = entry != null && entry.blockId.equals(blockId) ? entry.progress + amount : amount;
        long lastRepairTick = entry != null && entry.blockId.equals(blockId) ? entry.lastRepairTick : gameTime;
        damageByPosition.put(key, new Entry(blockId, total, lastRepairTick));
        setDirty();
        return total;
    }

    public Set<Long> repair(long gameTime, int intervalTicks) {
        if (intervalTicks <= 0) {
            return Set.of();
        }

        Set<Long> changed = new HashSet<>();
        Iterator<Map.Entry<Long, Entry>> iterator = damageByPosition.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, Entry> damage = iterator.next();
            Entry entry = damage.getValue();
            long elapsed = gameTime - entry.lastRepairTick;
            if (elapsed < intervalTicks) {
                continue;
            }

            long levels = elapsed / intervalTicks;
            float progress = Math.max(0.0f, entry.progress - levels);
            changed.add(damage.getKey());
            if (progress <= 0.0f) {
                iterator.remove();
            } else {
                damage.setValue(new Entry(entry.blockId, progress,
                        entry.lastRepairTick + levels * intervalTicks));
            }
        }

        if (!changed.isEmpty()) {
            setDirty();
        }
        return changed;
    }

    public float getProgress(BlockPos pos) {
        Entry entry = damageByPosition.get(pos.asLong());
        return entry == null ? 0.0f : entry.progress;
    }

    public boolean matches(BlockPos pos, BlockState state) {
        Entry entry = damageByPosition.get(pos.asLong());
        return entry != null && entry.blockId.equals(BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString());
    }

    public boolean clear(BlockPos pos) {
        if (damageByPosition.remove(pos.asLong()) != null) {
            setDirty();
            return true;
        }
        return false;
    }

    private record Entry(String blockId, float progress, long lastRepairTick) {
    }
}
