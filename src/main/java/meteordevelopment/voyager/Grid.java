package meteordevelopment.voyager;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Set;

import static meteordevelopment.voyager.Pathfinder.mc;

public class Grid implements Iterable<VChunk> {
    public int nodeCount;

    private final Long2ObjectMap<VChunk> chunks = new Long2ObjectOpenHashMap<>();

    private final Set<VChunk> dirtyChunks = new ObjectArraySet<>(); // Not using ObjectOpenHashSet because there is a bug that can occur when iterating over elements
    private final Set<VChunk> dirtyChunksConnections = new ObjectArraySet<>();

    public Grid(ChunkPos pos) {
        for (int x = pos.x - 7; x <= pos.x + 7; x++) {
            for (int z = pos.z - 7; z <= pos.z + 7; z++) {
                Chunk chunk = mc.world.getChunk(x, z);
                chunks.put(ChunkPos.toLong(x, z), new VChunk(this, chunk));
            }
        }

        for (VChunk chunk : chunks.values()) chunk.generateConnections();
    }

    public boolean updateChunks() {
        if (dirtyChunks.isEmpty()) return false;

        for (VChunk chunk : dirtyChunks) chunk.generateNodes();
        for (VChunk chunk : dirtyChunks) chunk.generateConnections();
        for (VChunk chunk : dirtyChunksConnections) chunk.generateConnections();

        dirtyChunks.clear();
        dirtyChunksConnections.clear();

        return true;
    }

    public VChunk getChunk(int x, int z) {
        return chunks.get(ChunkPos.toLong(x, z));
    }

    public Node getNode(int x, int y, int z) {
        if (y < 0 || y >= 256) return null;

        VChunk chunk = getChunk(x >> 4, z >> 4);
        return chunk != null ? chunk.get(x & 15, y, z & 15) : null;
    }

    public void stateChanged(int x, int y, int z) {
        int cx = x >> 4;
        int cz = z >> 4;

        VChunk chunk = getChunk(cx, cz);
        if (chunk != null) dirtyChunks.add(chunk);

        // TODO: Only check neighbours when close to an edge
        // Neighbours
        for (int x2 = -1; x2 <= 1; x2++) {
            for (int z2 = -1; z2 <= 1; z2++) {
                chunk = getChunk(cx + x2, cz + z2);
                if (chunk != null) dirtyChunksConnections.add(chunk);
            }
        }
    }

    @NotNull
    @Override
    public Iterator<VChunk> iterator() {
        return chunks.values().iterator();
    }
}
