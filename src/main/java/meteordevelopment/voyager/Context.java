package meteordevelopment.voyager;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;

import static meteordevelopment.voyager.Voyager.mc;

public class Context {
    private final World world;

    private Chunk lastChunk;
    private final BlockPos.Mutable pos = new BlockPos.Mutable();

    public Context(World world) {
        this.world = world;
    }

    public BlockState getBlockState(int x, int y, int z) {
        if (world.isOutOfHeightLimit(y)) return Blocks.VOID_AIR.getDefaultState();

        int cx = x >> 4;
        int cz = z >> 4;

        Chunk chunk;

        if (lastChunk != null && lastChunk.getPos().x == cx && lastChunk.getPos().z == cz) chunk = lastChunk;
        else chunk = world.getChunk(cx, cz, ChunkStatus.FULL, false);

        if (chunk == null) return Blocks.VOID_AIR.getDefaultState();

        ChunkSection section = chunk.getSectionArray()[y >> 4];
        if (section == null) return Blocks.VOID_AIR.getDefaultState();

        lastChunk = chunk;
        return section.getBlockState(x & 15, y & 15, z & 15);
    }

    public boolean canWalkThrough(int x, int y, int z) {
        BlockState state = getBlockState(x, y, z);

        if (state.isAir()) return true;
        if (!state.getFluidState().isEmpty()) return false;

        return state.getCollisionShape(mc.world, pos.set(x, y, z)).isEmpty();
    }

    public boolean canWalkOn(int x, int y, int z) {
        BlockState state = getBlockState(x, y, z);

        if (state.isAir()) return false;
        if (!state.getFluidState().isEmpty()) return false;

        return !state.getCollisionShape(mc.world, pos.set(x, y, z)).isEmpty();
    }
}
