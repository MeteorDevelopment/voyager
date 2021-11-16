package meteordevelopment.voyager.goals;

public class XZGoal implements IGoal {
    private final int x, z;

    public XZGoal(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public boolean isInGoal(int x, int y, int z) {
        return x == this.x && z == this.z;
    }

    @Override
    public float heuristic(int x, int y, int z) {
        float dx = this.x + 0.5f - x;
        float dz = this.z + 0.5f - z;
        return (float) Math.sqrt(dx * dx + dz * dz);
    }
}
