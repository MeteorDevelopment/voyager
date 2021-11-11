package meteordevelopment.voyager;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

import static meteordevelopment.voyager.Pathfinder.mc;

public class Grid implements Iterable<VChunk> {
    public int nodeCount;
    private final Long2ObjectMap<VChunk> chunks = new Long2ObjectOpenHashMap<>();

    public Grid(ChunkPos pos) {
        for (int x = pos.x - 7; x <= pos.x + 7; x++) {
            for (int z = pos.z - 7; z <= pos.z + 7; z++) {
                Chunk chunk = mc.world.getChunk(x, z);
                chunks.put(ChunkPos.toLong(x, z), new VChunk(this, chunk));
            }
        }

        for (VChunk chunk : chunks.values()) chunk.generateConnections();
    }

    public VChunk getChunk(int x, int z) {
        return chunks.get(ChunkPos.toLong(x, z));
    }

    public Node getNode(int x, int y, int z) {
        if (y < 0 || y >= 256) return null;

        VChunk chunk = getChunk(x >> 4, z >> 4);
        return chunk != null ? chunk.get(x & 15, y, z & 15) : null;
    }

    @NotNull
    @Override
    public Iterator<VChunk> iterator() {
        return chunks.values().iterator();
    }
}
