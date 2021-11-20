package meteordevelopment.voyager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import static meteordevelopment.voyager.Voyager.mc;

public class MoveGenerator {
    private static final Block[] BLOCKS_THAT_MAKE_YOU_GO_OUCH = { Blocks.CACTUS, Blocks.FIRE, Blocks.SOUL_FIRE, Blocks.SWEET_BERRY_BUSH, Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE };
    private static final float SQRT_2 = (float) Math.sqrt(2);

    private static final BlockPos.Mutable pos = new BlockPos.Mutable();

    private final IWorldInterface wi;
    private int x, y, z;
    private int moveI;

    private boolean canWalkThroughDidHurtOrIdk;

    public MoveType moveType;
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

        if (!canStandIn(x, y, z)) moveI = Integer.MAX_VALUE;
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

    // Checking methods

    private boolean checkStraight(int dx, int dz) {
        if (wi.isOutside(x + dx, z + dz)) return outside();

        if (!canStandIn(x + dx, y, z + dz)) return false;

        return move(MoveType.Straight, x + dx, y, z + dz, 1);
    }

    private boolean checkDiagonal(int dx, int dz) {
        if (wi.isOutside(x + dx, z + dz)) return outside();

        if (!canStandIn(x + dx, y, z + dz)) return false;

        boolean canX = canWalkThrough(x + dx, y, z, 2, true);
        if (canWalkThroughDidHurtOrIdk) return false;

        boolean canZ = canWalkThrough(x, y, z + dz, 2, true);
        if (canWalkThroughDidHurtOrIdk) return false;

        if (!canX && !canZ) return false;

        if (canX && canZ) return move(MoveType.Straight, x + dx, y, z + dz, SQRT_2);
        return move(MoveType.CornerBump, x + dx, y, z + dz, 2.5f);
    }

    private boolean checkStep(boolean up, int dx, int dz, float cost) {
        if (wi.isOutside(x + dx, z + dz)) return outside();

        if (up) {
            if (!canWalkThrough(x, y + 2, z)) return false;

            if (canStandIn(x + dx, y + 1, z + dz)) return move(MoveType.Jump, x + dx, y + 1, z + dz, cost);
        }
        else {
            if (canStandIn(x + dx, y - 1, z + dz, 3)) return move(MoveType.Straight, x + dx, y - 1, z + dz, cost);
        }

        return false;
    }

    private boolean checkJump1(int dx, int dz) {
        if (wi.isOutside(x + dx, z + dz)) return outside();

        if (!canWalkThrough(x, y + 2, z)) return false;
        if (canWalkOn(x + dx / 2, y - 1, z + dz / 2)) return false;
        if (!canWalkThrough(x + dx / 2, y, z + dz / 2, 3)) return false;
        if (!canStandIn(x + dx, y, z + dz, 3)) return false;

        return move(MoveType.Jump1, x + dx, y, z + dz, 3.25f);
    }

    // Helper methods

    private boolean canStandIn(int x, int y, int z, int height) {
        if (!canWalkOn(x, y - 1, z)) return false;
        return canWalkThrough(x, y, z, height);
    }
    private boolean canStandIn(int x, int y, int z) {
        return canStandIn(x, y, z, 2);
    }

    private boolean canWalkOn(int x, int y, int z) {
        BlockState state = wi.getBlockState(x, y, z);

        if (state.isAir()) return false;
        if (!state.getFluidState().isEmpty()) return false;
        if (state.getCollisionShape(mc.world, pos.set(x, y, z)).isEmpty()) return false;

        for (Block block : BLOCKS_THAT_MAKE_YOU_GO_OUCH) {
            if (state.getBlock() == block) return false;
        }

        return true;
    }

    private boolean canWalkThrough(int x, int y, int z, int height, boolean setThingAtFluid) {
        canWalkThroughDidHurtOrIdk = false;

        for (int i = 0; i < height; i++) {
            if (!canWalkThrough(x, y + i, z, setThingAtFluid)) return false;
        }

        return true;
    }
    private boolean canWalkThrough(int x, int y, int z, int height) {
        return canWalkThrough(x, y, z, height, false);
    }

    private boolean canWalkThrough(int x, int y, int z, boolean setThingAtFluid) {
        BlockState state = wi.getBlockState(x, y, z);

        if (state.isAir()) return true;

        for (Block block : BLOCKS_THAT_MAKE_YOU_GO_OUCH) {
            if (state.getBlock() == block) {
                canWalkThroughDidHurtOrIdk = true;
                return false;
            }
        }

        if (!state.getFluidState().isEmpty()) {
            if (setThingAtFluid) canWalkThroughDidHurtOrIdk = true;
            return false;
        }

        return state.getCollisionShape(mc.world, pos.set(x, y, z)).isEmpty();
    }
    private boolean canWalkThrough(int x, int y, int z) {
        return canWalkThrough(x, y, z, false);
    }

    private boolean move(MoveType type, int x, int y, int z, float cost) {
        moveType = type;
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
