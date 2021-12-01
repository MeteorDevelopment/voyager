package meteordevelopment.voyager.goals;

import net.minecraft.util.math.BlockPos;

public class XYZGoal implements IGoal {
    private final int x, y, z;

    public XYZGoal(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public XYZGoal(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    @Override
    public boolean isInGoal(int x, int y, int z) {
        return x == this.x && y == this.y && z == this.z;
    }

    @Override
    public float heuristic(int x, int y, int z) {
        float dx = this.x - x;
        float dy = this.y - y;
        float dz = this.z - z;
        return (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
