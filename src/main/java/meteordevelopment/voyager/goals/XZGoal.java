package meteordevelopment.voyager.goals;

import net.minecraft.entity.Entity;

public class XZGoal implements IGoal {
    private final int x, z;

    public XZGoal(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public XZGoal(Entity entity, int distance) {
        double yaw = Math.toRadians(entity.getYaw());
        double x = entity.getX() - Math.sin(yaw) * distance;
        double z = entity.getZ() + Math.cos(yaw) * distance;

        this.x = (int) Math.floor(x);
        this.z = (int) Math.floor(z);
    }

    @Override
    public boolean isInGoal(int x, int y, int z) {
        return x == this.x && z == this.z;
    }

    @Override
    public float heuristic(int x, int y, int z) {
        float dx = this.x - x;
        float dz = this.z - z;
        return (float) Math.sqrt(dx * dx + dz * dz);
    }
}
