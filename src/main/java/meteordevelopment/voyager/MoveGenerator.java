package meteordevelopment.voyager;

import java.util.List;

public class MoveGenerator {
    /** Generates all possible moves from a position. */
    public static void generate(Context ctx, int x, int y, int z, List<Move> moves) {
        if (!ctx.canWalkOn(x, y - 1, z) || !canWalkThrough(ctx, x, y, z)) return;

        // Forward
        checkForward(ctx, x + 1, y, z, moves);
        checkForward(ctx, x - 1, y, z, moves);
        checkForward(ctx, x, y, z + 1, moves);
        checkForward(ctx, x, y, z - 1, moves);

        // Diagonal
        checkDiagonal(ctx, x, y, z, 1, 1, moves);
        checkDiagonal(ctx, x, y, z, -1, -1, moves);
        checkDiagonal(ctx, x, y, z, 1, -1, moves);
        checkDiagonal(ctx, x, y, z, -1, 1, moves);

        // Jump 1 forward
        checkJump1(ctx, x, y, z, 2, 0, moves);
        checkJump1(ctx, x, y, z, -2, 0, moves);
        checkJump1(ctx, x, y, z, 0, 2, moves);
        checkJump1(ctx, x, y, z, 0, -2, moves);
    }

    private static void checkForward(Context ctx, int x, int y, int z, List<Move> moves) {
        if (ctx.canWalkOn(x, y - 1, z) && canWalkThrough(ctx, x, y, z)) moves.add(new Move(Move.Type.Forward, x, y, z, 1));
        else if (ctx.canWalkOn(x, y - 2, z) && canWalkThrough(ctx, x, y - 1, z)) moves.add(new Move(Move.Type.Forward, x, y - 1, z, 1));
        else if (ctx.canWalkOn(x, y, z) && canWalkThrough(ctx, x, y + 1, z)) moves.add(new Move(Move.Type.Jump, x, y + 1, z, 3.25f));
    }

    private static void checkDiagonal(Context ctx, int x, int y, int z, int dx, int dz, List<Move> moves) {
        if (!ctx.canWalkOn(x + dx, y - 1, z + dz) || !canWalkThrough(ctx, x + dx, y, z + dz)) return;

        boolean canX = canWalkThrough(ctx, x + dx, y, z);
        boolean canZ = canWalkThrough(ctx, x, y, z + dz);
        if (!canX && !canZ) return;

        if (canX && canZ) moves.add(new Move(Move.Type.Forward, x + dx, y, z + dz, 1));
        else moves.add(new Move(Move.Type.CornerBump, x + dx, y, z + dz, 2.5f));
    }

    private static void checkJump1(Context ctx, int x, int y, int z, int dx, int dz, List<Move> moves) {
        if (!ctx.canWalkOn(x + dx, y - 1, z + dz) || !canWalkThrough(ctx, x + dx, y, z + dz)) return;

        if (!ctx.canWalkOn(x + dx / 2, y - 1, z + dz / 2) && canWalkThrough(ctx, x + dx / 2, y, z + dz / 2)) moves.add(new Move(Move.Type.Jump, x + dx, y, z + dz, 3.25f));
    }

    private static boolean canWalkThrough(Context ctx, int x, int y, int z) {
        return ctx.canWalkThrough(x, y, z) && ctx.canWalkThrough(x, y + 1, z);
    }
}
