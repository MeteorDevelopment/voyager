package meteordevelopment.voyager;

import meteordevelopment.voyager.pathfinder.Path;
import meteordevelopment.voyager.utils.Utils;
import net.minecraft.client.input.Input;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.voyager.Voyager.mc;

public class VInput extends Input {
    private final Path path;
    private Path.Step current, next;
    private boolean isNew;

    private boolean waitForGround;
    private float yaw, prevYaw;

    public VInput(Path path) {
        this.path = path;

        prevYaw = yaw = mc.player.getYaw();

        next = path.start();
        mc.player.setYaw(getNextYaw());
    }

    private void tickBase() {
        movementForward = pressingForward == pressingBack ? 0.0F : (pressingForward ? 1.0F : -1.0F);
        movementSideways = pressingLeft == pressingRight ? 0.0F : (pressingLeft ? 1.0F : -1.0F);

        if (sneaking) {
            movementForward *= 0.3;
            movementSideways *= 0.3;
        }
    }

    @Override
    public void tick(boolean slowDown, float f) {
        pressingForward = true;
        jumping = false;

        if (waitForGround) {
            if (mc.player.isOnGround()) waitForGround = false;
            else {
                tickBase();
                return;
            }
        }

        boolean sprinting = true;

        if (isNew) {
            // Jump or Jump1
            if (next.type == MoveType.Jump || next.type == MoveType.Jump1) {
                if (!mc.player.isOnGround()) {
                    waitForGround = true;
                    return;
                }

                sprinting = false;
                jumping = true;
            }
        }

        mc.player.setYaw(getNextYaw());
        mc.player.setSprinting(sprinting);

        isNew = false;
        tickBase();
    }

    private float getNextYaw() {
        return (float) Utils.getYaw(next.x + 0.5, next.z + 0.5);
    }

    private double getDistanceToNext(Vec3d vec) {
        double dx = (next.x + 0.5) - (mc.player.getX() + vec.x);
        double dz = (next.z + 0.5) - (mc.player.getZ() + vec.z);

        return Math.sqrt(dx * dx + dz * dz);
    }

    private boolean next() {
        if (next.next == null) return false;

        current = next;
        next = next.next;
        isNew = true;

        return true;
    }

    private boolean modified, modifiedX, modifiedZ;
    private boolean stop;

    public void limitMovement(Vec3d vec) {
        modified = modifiedX = modifiedZ = false;

        double x = mc.player.getX() + vec.x;
        double z = mc.player.getZ() + vec.z;

        double nextX = next.x + 0.5;
        double nextZ = next.z + 0.5;

        Vec3d vec2 = new Vec3d(vec.x, vec.y, vec.z);

        if (vec2.x > 0) {
            if (x > nextX) {
                vec2.x = nextX - mc.player.getX();
                modifiedX = true;
            }
        } else if (vec2.x < 0) {
            if (x < nextX) {
                vec2.x = -(mc.player.getX() - nextX);
                modifiedX = true;
            }
        }

        if (vec2.z > 0) {
            if (z > nextZ) {
                vec2.z = nextZ - mc.player.getZ();
                modifiedZ = true;
            }
        } else if (vec2.z < 0) {
            if (z < nextZ) {
                vec2.z = -(mc.player.getZ() - nextZ);
                modifiedZ = true;
            }
        }

        double distance = getDistanceToNext(vec2);
        if (distance <= 0.25) {
            Path.Step prev = current;

            if (!next()) stop = true;
            else if (prev != null) {
                if (current.y == next.y && current.x + prev.getDirX(current) == next.x&& current.z+ prev.getDirZ(current) == next.z) return;
            }
        }

        vec.x = vec2.x;
        vec.z = vec2.z;

        modified = true;
    }

    public void afterMove() {
        if (!modified) return;

        Vec3d velocity = mc.player.getVelocity();

        if (modifiedX) velocity.x = 0;
        if (modifiedZ) velocity.z = 0;

        if (stop) {
            Voyager.INSTANCE.stop();
            mc.player.setYaw(yaw);

            path.continueIfNeeded();
        }
    }

    public void changeLookDirection(double deltaX) {
        prevYaw = yaw;
        yaw += deltaX;
    }

    public float getYaw(float tickDelta) {
        return MathHelper.lerp(tickDelta, prevYaw, yaw);
    }

        /*
        modifiedX = modifiedZ = false;

        double x = mc.player.getX() + vec.x;
        double z = mc.player.getZ() + vec.z;

        double nextX = next.x() + 0.5;
        double nextZ = next.z() + 0.5;

        if (vec.x > 0) {
            if (x > nextX) {
                ((IVec3d) vec).setXZ(nextX - mc.player.getX(), vec.z);
                modifiedX = true;
            }
        }
        else if (vec.x < 0) {
            if (x < nextX) {
                ((IVec3d) vec).setXZ(-(mc.player.getX() - nextX), vec.z);
                modifiedX = true;
            }
        }

        if (vec.z > 0) {
            if (z > nextZ) {
                ((IVec3d) vec).setXZ(vec.x, nextZ - mc.player.getZ());
                modifiedZ = true;
            }
        }
        else if (vec.z < 0) {
            if (z < nextZ) {
                ((IVec3d) vec).setXZ(vec.x, -(mc.player.getZ() - nextZ));
                modifiedZ = true;
            }
        }
         */
}
