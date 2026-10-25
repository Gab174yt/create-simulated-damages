package dev.simulateddamages;

import java.util.HashMap;
import java.util.Map;
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
                    new Entry(entry.getString("block"), entry.getFloat("damage")));
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
            savedEntry.putFloat("damage", entry.damage);
            entries.add(savedEntry);
        });
        tag.put(DAMAGE_LIST, entries);
        return tag;
    }

    public float add(BlockPos pos, BlockState state, float amount) {
        long key = pos.asLong();
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        Entry entry = damageByPosition.get(key);
        float total = entry != null && entry.blockId.equals(blockId) ? entry.damage + amount : amount;
        damageByPosition.put(key, new Entry(blockId, total));
        setDirty();
        return total;
    }

    public boolean clear(BlockPos pos) {
        if (damageByPosition.remove(pos.asLong()) != null) {
            setDirty();
            return true;
        }
        return false;
    }

    private record Entry(String blockId, float damage) {
    }
}
