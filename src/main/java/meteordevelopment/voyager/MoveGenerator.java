package meteordevelopment.voyager;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import static meteordevelopment.voyager.Voyager.mc;

public class MoveGenerator {
    private static final BlockPos.Mutable pos = new BlockPos.Mutable();

    private final IWorldInterface wi;
    private int x, y, z;
    private int moveI;

    public int moveX, moveY, moveZ;
    public boolean moveOutside;
    public float moveCost;

    public MoveGenerator(IWorldInterface wi) {
        this.wi = wi;
    }

    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.moveI = 0;
    }

    public boolean hasNext() {
        return moveI < 20;
    }

    public boolean next() {
        return switch (moveI++) {
            // Straight
            case 0 -> checkStraight(1, 0);
            case 1 -> checkStraight(-1, 0);
            case 2 -> checkStraight(0, 1);
            case 3 -> checkStraight(0, -1);

            // Diagonal
            case 4 -> checkDiagonal(1, 1);
            case 5 -> checkDiagonal(-1, -1);
            case 6 -> checkDiagonal(1, -1);
            case 7 -> checkDiagonal(-1, 1);

            // Step up
            case 8 -> checkStep(true, 1, 0, 3.25f);
            case 9 -> checkStep(true, -1, 0, 3.25f);
            case 10 -> checkStep(true, 0, 1, 3.25f);
            case 11 -> checkStep(true, 0, -1, 3.25f);

            // Step down
            case 12 -> checkStep(false, 1, 0, 1);
            case 13 -> checkStep(false, -1, 0, 1);
            case 14 -> checkStep(false, 0, 1, 1);
            case 15 -> checkStep(false, 0, -1, 1);

            // Jump 1
            case 16 -> checkJump1(2, 0);
            case 17 -> checkJump1(-2, 0);
            case 18 -> checkJump1(0, 2);
            case 19 -> checkJump1(0, -2);

            default -> false;
        };
    }

    private boolean checkStraight(int dx, int dz) {
        if (wi.isOutside(x + dx, z + dz)) return outside();

        if (canWalkOn(x + dx, y - 1, z + dz) && canWalkThrough(x + dx, y, z + dz)) return move(x + dx, y, z + dz, 1);
        return false;
    }

    private boolean checkDiagonal(int dx, int dz) {
        if (wi.isOutside(x + dx, z + dz)) return outside();

        if (!canWalkOn(x + dx, y - 1, z + dz) || !canWalkThrough(x + dx, y, z + dz)) return false;

        boolean canX = canWalkThrough(x + dx, y, z);
        boolean canZ = canWalkThrough(x, y, z + dz);
        if (!canX && !canZ) return false;

        float cost;
        if (canX && canZ) cost = 1;
        else cost = 2.5f;

        return move(x + dx, y, z + dz, cost);
    }

    private boolean checkStep(boolean up, int dx, int dz, float cost) {
        if (wi.isOutside(x + dx, z + dz)) return outside();

        if (up) {
            if (canWalkOn(x + dx, y - 2, z) && canWalkThrough(x + dx, y - 1, z + dz)) return move(x + dx, y - 1, z + dz, cost);
        }
        else {
            if (canWalkOn(x + dx, y, z + dz) && canWalkThrough(x + dx, y + 1, z + dz)) return move(x + dx, y + 1, z + dz, cost);
        }

        return false;
    }

    private boolean checkJump1(int dx, int dz) {
        if (wi.isOutside(x + dx, z + dz)) return outside();

        if (!canWalkOn(x + dx, y - 1, z + dz) || !canWalkThrough(x + dx, y, z + dz)) return false;

        if (!canWalkOn(x + dx / 2, y - 1, z + dz / 2) && canWalkThrough(x + dx / 2, y, z + dz / 2)) return move(x + dx, y, z + dz, 3.25f);

        return false;
    }

    private boolean canWalkThrough(int x, int y, int z) {
        return canWalkThroughBlock(x, y, z) && canWalkThroughBlock(x, y + 1, z);
    }

    private boolean canWalkThroughBlock(int x, int y, int z) {
        BlockState state = wi.getBlockState(x, y, z);

        if (state.isAir()) return true;
        if (!state.getFluidState().isEmpty()) return false;

        return state.getCollisionShape(mc.world, pos.set(x, y, z)).isEmpty();
    }

    private boolean canWalkOn(int x, int y, int z) {
        BlockState state = wi.getBlockState(x, y, z);

        if (state.isAir()) return false;
        if (!state.getFluidState().isEmpty()) return false;

        return !state.getCollisionShape(mc.world, pos.set(x, y, z)).isEmpty();
    }

    private boolean move(int x, int y, int z, float cost) {
        moveX = x;
        moveY = y;
        moveZ = z;
        moveOutside = false;
        moveCost = cost;
        return true;
    }

    private boolean outside() {
        moveOutside = true;
        return false;
    }
}
