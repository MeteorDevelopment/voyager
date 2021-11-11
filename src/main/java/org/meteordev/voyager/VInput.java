package org.meteordev.voyager;

import org.meteordev.voyager.utils.Utils;
import net.minecraft.client.input.Input;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class VInput extends Input {
    private final List<Node> path;
    private Node current, next;
    private boolean isNew;
    private int i;

    private boolean waitForGround;
    private float yaw, prevYaw;

    public VInput(List<Node> path) {
        this.path = path;

        prevYaw = yaw = Pathfinder.mc.player.getYaw();

        next();
        Pathfinder.mc.player.setYaw(getNextYaw());
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
    public void tick(boolean slowDown) {
        pressingForward = true;
        jumping = false;

        if (waitForGround) {
            if (Pathfinder.mc.player.isOnGround()) waitForGround = false;
            else {
                tickBase();
                return;
            }
        }

        boolean sprinting = true;

        if (isNew) {
            // Jump or Jump1
            if (is(1, 1) || is(2, 0)) {
                if (!Pathfinder.mc.player.isOnGround()) {
                    waitForGround = true;
                    return;
                }

                sprinting = false;
                jumping = true;
            }
        }

        Pathfinder.mc.player.setYaw(getNextYaw());
        Pathfinder.mc.player.setSprinting(sprinting);

        isNew = false;
        tickBase();
    }

    private boolean is(int h, int y) {
        if (current == null || next.y() != current.y() + y) return false;

        double dx = next.x() - current.x();
        double dz = next.z() - current.z();
        double dist = Math.sqrt(dx * dx + dz * dz);

        return dist == h;
    }

    private float getNextYaw() {
        return (float) Utils.getYaw(next.x() + 0.5, next.z() + 0.5);
    }

    private double getDistanceToNext(Vec3d vec) {
        double dx = (next.x() + 0.5) - (Pathfinder.mc.player.getX() + vec.x);
        double dz = (next.z() + 0.5) - (Pathfinder.mc.player.getZ() + vec.z);

        return Math.sqrt(dx * dx + dz * dz);
    }

    private boolean next() {
        if (i >= path.size()) return false;

        current = next;
        next = path.get(i++);
        isNew = true;

        return true;
    }

    private boolean modified, modifiedX, modifiedZ;
    private boolean stop;

    public void limitMovement(Vec3d vec) {
        modified = modifiedX = modifiedZ = false;

        double x = Pathfinder.mc.player.getX() + vec.x;
        double z = Pathfinder.mc.player.getZ() + vec.z;

        double nextX = next.x() + 0.5;
        double nextZ = next.z() + 0.5;

        double dx = Math.abs(x - nextX);
        double dz = Math.abs(z - nextZ);

        Vec3d vec2 = new Vec3d(vec.x, vec.y, vec.z);

        if (vec2.x > 0) {
            if (x > nextX) {
                vec2.x = nextX - Pathfinder.mc.player.getX();
                modifiedX = true;
            }
        } else if (vec2.x < 0) {
            if (x < nextX) {
                vec2.x = -(Pathfinder.mc.player.getX() - nextX);
                modifiedX = true;
            }
        }

        if (vec2.z > 0) {
            if (z > nextZ) {
                vec2.z = nextZ - Pathfinder.mc.player.getZ();
                modifiedZ = true;
            }
        } else if (vec2.z < 0) {
            if (z < nextZ) {
                vec2.z = -(Pathfinder.mc.player.getZ() - nextZ);
                modifiedZ = true;
            }
        }

        double distance = getDistanceToNext(vec2);
        if (distance <= 0.01) {
            Node prev = current;

            if (!next()) stop = true;
            else if (prev != null) {
                if (current.y() == next.y() && current.x() + Path.getDirX(prev, current) == next.x() && current.z() + Path.getDirZ(prev, current) == next.z()) return;
            }
        }

        vec.x = vec2.x;
        vec.z = vec2.z;

        modified = true;
    }

    public void afterMove() {
        if (!modified) return;

        Vec3d velocity = Pathfinder.mc.player.getVelocity();

        if (modifiedX) velocity.x = 0;
        if (modifiedZ) velocity.z = 0;

        if (stop) {
            Pathfinder.stopMovement();
            Pathfinder.mc.player.setYaw(yaw);
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