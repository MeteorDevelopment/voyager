package meteordevelopment.voyager;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;

public class WorldInterfaceImpl implements IWorldInterface {
    private final World world;

    private Chunk lastChunk;

    public WorldInterfaceImpl(World world) {
        this.world = world;
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        if (world.isOutOfHeightLimit(y)) return Blocks.VOID_AIR.getDefaultState();

        int cx = x >> 4;
        int cz = z >> 4;

        Chunk chunk;

        if (lastChunk != null && lastChunk.getPos().x == cx && lastChunk.getPos().z == cz) chunk = lastChunk;
        else chunk = world.getChunk(cx, cz, ChunkStatus.FULL, false);

        if (chunk == null) return Blocks.VOID_AIR.getDefaultState();

        ChunkSection section = chunk.getSectionArray()[chunk.getSectionIndex(y)];
        if (section == null) return Blocks.VOID_AIR.getDefaultState();

        lastChunk = chunk;
        return section.getBlockState(x & 15, y & 15, z & 15);
    }

    @Override
    public boolean isOutside(int x, int z) {
        int cx = x >> 4;
        int cz = z >> 4;

        if (lastChunk != null && lastChunk.getPos().x == cx && lastChunk.getPos().z == cz) return false;
        return world.getChunk(cx, cz, ChunkStatus.FULL, false) == null;
    }
}
