package meteordevelopment.voyager;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static meteordevelopment.voyager.Pathfinder.mc;

public class VChunk implements Iterable<Node> {
    private final Grid grid;
    private final Chunk chunk;

    private final Node[][][] nodes = new Node[16][256][16];
    private final List<Node> nodeList = new ArrayList<>();

    private final BlockPos.Mutable pos = new BlockPos.Mutable();

    public VChunk(Grid grid, Chunk chunk) {
        this.grid = grid;
        this.chunk = chunk;

        generateNodes();
    }

    public void generateNodes() {
        if (!nodeList.isEmpty()) {
            grid.nodeCount -= nodeList.size();
            nodeList.clear();

            // TODO: This is horrible
            for (int x = 0; x < 16; x++) {
                for (int y = 0; y < 256; y++) {
                    for (int z = 0; z < 16; z++) {
                        nodes[x][y][z] = null;
                    }
                }
            }
        }

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int h = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x, z) + 2;

                for (int y = 0; y < h; y++) {
                    if (!isValid(x, y, z)) continue;

                    Node node = new Node(chunk.getPos().x * 16 + x, y, chunk.getPos().z * 16 + z, new ArrayList<>());
                    grid.nodeCount++;

                    nodes[x][y][z] = node;
                    nodeList.add(node);
                }
            }
        }
    }

    public void generateConnections() {
        for (Node node : nodeList) {
            int x = node.x() & 15;
            int y = node.y();
            int z = node.z() & 15;

            node.connections().clear();

            // Neighbours
            for (int i = -1; i <= 1; i++) {
                Node n = getChecked(x + 1, y + i, z);
                if (n != null) node.connections().add(new Connection(i > 0 ? Connection.Type.Jump : Connection.Type.Straight, n));

                n = getChecked(x - 1, y + i, z);
                if (n != null) node.connections().add(new Connection(i > 0 ? Connection.Type.Jump : Connection.Type.Straight, n));

                n = getChecked(x, y + i, z + 1);
                if (n != null) node.connections().add(new Connection(i > 0 ? Connection.Type.Jump : Connection.Type.Straight, n));

                n = getChecked(x, y + i, z - 1);
                if (n != null) node.connections().add(new Connection(i > 0 ? Connection.Type.Jump : Connection.Type.Straight, n));
            }

            // Diagonals
            checkDiagonal(x, y, z, 1, 1, node);
            checkDiagonal(x, y, z, -1, -1, node);
            checkDiagonal(x, y, z, -1, 1, node);
            checkDiagonal(x, y, z, 1, -1, node);

            // Jump one forward
            Node n = getChecked(x + 2, y, z);
            if (n != null && canWalkThrough(x + 1, y - 1, z, false) && canWalkThrough(x + 1, y, z)) node.connections().add(new Connection(Connection.Type.Jump1, n));

            n = getChecked(x - 2, y, z);
            if (n != null && canWalkThrough(x - 1, y - 1, z, false) && canWalkThrough(x - 1, y, z)) node.connections().add(new Connection(Connection.Type.Jump1, n));

            n = getChecked(x, y, z + 2);
            if (n != null && canWalkThrough(x, y - 1, z + 1, false) && canWalkThrough(x, y, z + 1)) node.connections().add(new Connection(Connection.Type.Jump1, n));

            n = getChecked(x, y, z - 2);
            if (n != null && canWalkThrough(x, y - 1, z - 1, false) && canWalkThrough(x, y, z - 1)) node.connections().add(new Connection(Connection.Type.Jump1, n));
        }
    }

    private boolean canWalkThrough(int x, int y, int z, boolean includeFluids) {
        BlockState state = getState(x, y, z);

        if (state.isAir()) return true;
        if (includeFluids && !state.getFluidState().isEmpty()) return false;

        return state.getCollisionShape(mc.world, pos).isEmpty();
    }
    private boolean canWalkThrough(int x, int y, int z) {
        return canWalkThrough(x, y, z, true);
    }

    private boolean isValid(int x, int y, int z) {
        if (!canWalkThrough(x, y, z)) return false;

        if (canWalkThrough(x, y - 1, z, false)) return false;

        return canWalkThrough(x, y + 1, z);
    }

    private void checkDiagonal(int x, int y, int z, int dx, int dz, Node node) {
        Node n = getChecked(x + dx, y, z + dz);
        if (n == null) return;

        boolean x1 = canWalkThrough(x + dx, y, z);
        boolean z1 = canWalkThrough(x, y, z + dz);
        if (!x1 && !z1) return;

        boolean x2 = canWalkThrough(x + dx, y + 1, z);
        boolean z2 = canWalkThrough(x, y + 1, z + dz);
        if ((!x2 && !z2) || (!x1 && !z2) || (!x2 && !z1)) return;

        Connection.Type type = Connection.Type.CornerBump;
        if (x1 && x2 && z1 && z2) type = Connection.Type.Straight;

        node.connections().add(new Connection(type, n));
    }

    private Node getCorner(int x, int y, int z, int dx, int dz) {
        Node n = getChecked(x + dx, y, z + dz);

        if (n != null && canWalkThrough(x + dx, y, z) && canWalkThrough(x, y, z + dz) && canWalkThrough(x + dx, y + 1, z) && canWalkThrough(x, y + 1, z + dz)) return n;
        return null;
    }

    private Node getChecked(int x, int y, int z) {
        if (y < 0 || y >= 256) return null;

        if (x >= 0 && x < 16 && z >= 0 && z < 16) return nodes[x][y][z];

        int dx = x < 0 ? -1 : (x >= 16 ? 1 : 0);
        int dz = z < 0 ? -1 : (z >= 16 ? 1 : 0);

        VChunk chunk = grid.getChunk(this.chunk.getPos().x + dx, this.chunk.getPos().z + dz);
        if (chunk == null) return null;

        return chunk.get(x & 15, y, z & 15);
    }

    private BlockState getState(int x, int y, int z) {
        if (y < 0 || y >= 256) return Blocks.VOID_AIR.getDefaultState();

        if (x >= 0 && x < 16 && z >= 0 && z < 16) return chunk.getBlockState(pos.set(x, y, z));

        return mc.world.getBlockState(pos.set(chunk.getPos().x * 16 + x, y, chunk.getPos().z * 16 + z));
    }

    public Node get(int x, int y, int z) {
        return nodes[x][y][z];
    }

    @NotNull
    @Override
    public Iterator<Node> iterator() {
        return nodeList.iterator();
    }
}
