package meteordevelopment.voyager.goals;

import meteordevelopment.voyager.Voyager;

public class DirectionGoal implements IGoal {
    private final double yaw;
    private int x, z;

    public DirectionGoal(float yaw) {
        this.x = (int) Math.floor(Voyager.mc.player.getX());
        this.z = (int) Math.floor(Voyager.mc.player.getZ());
        this.yaw = Math.toRadians(yaw);

        recalculate();
    }

    private void recalculate() {
        x = (int) Math.floor(x - Math.sin(yaw) * 50);
        z = (int) Math.floor(z + Math.cos(yaw) * 50);
    }

    @Override
    public boolean isInGoal(int x, int y, int z) {
        return x == this.x && z == this.z;
    }

    @Override
    public float heuristic(int x, int y, int z) {
        float dx = this.x + 0.5f - x;
        float dz = this.z + 0.5f - z;
        double distance = Math.sqrt(dx * dx + dz * dz);

        if (distance < 25) {
            recalculate();

            dx = this.x + 0.5f - x;
            dz = this.z + 0.5f - z;
            distance = Math.sqrt(dx * dx + dz * dz);
        }

        return (float) distance;
    }
}
