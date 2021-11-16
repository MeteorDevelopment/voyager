package meteordevelopment.voyager;

import meteordevelopment.voyager.goals.XYZGoal;
import meteordevelopment.voyager.pathfinder.Node;
import meteordevelopment.voyager.utils.Color;
import meteordevelopment.voyager.utils.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static meteordevelopment.voyager.Voyager.mc;
import static org.lwjgl.glfw.GLFW.*;

public class NoName {
    private static final Renderer renderer = new Renderer();
    private static final Color lineColor = new Color(255, 255, 255, 50);
    private static final Color startColor = new Color(25, 25, 225, 175);
    private static final Color endColor = new Color(225, 25, 25, 175);
    private static final Color pathColor = new Color(25, 225, 25, 255);

    private static BlockPos start, end;

    private static void findPath() {
        if (end == null) return;

        if (start != null) Voyager.INSTANCE.findPath(start, new XYZGoal(end));
        else Voyager.INSTANCE.moveTo(new XYZGoal(end));
    }

    public static void render(MatrixStack matrices) {
        int x = mc.player.getBlockX();
        int y = mc.player.getBlockY();
        int z = mc.player.getBlockZ();

        MoveGenerator moves = new MoveGenerator(Voyager.INSTANCE.getWorldInterface());
        renderer.begin(matrices, false, false);

        moves.set(x, y, z);
        while (moves.hasNext()) {
            if (!moves.next()) continue;

            renderer.line(x + 0.5, y + 0.5, z + 0.5, moves.moveX + 0.5, moves.moveY + 0.5, moves.moveZ + 0.5, lineColor);
        }

        if (start != null) renderer.box(start.getX() + 0.4, start.getY() + 0.4, start.getZ() + 0.4, 0.2, startColor);
        if (end != null) renderer.box(end.getX() + 0.4, end.getY() + 0.4, end.getZ() + 0.4, 0.2, endColor);

        List<Node> path = Voyager.INSTANCE.getLastPath();
        if (path.size() > 0) {
            Node first = path.get(0);

            for (int i = 1; i < path.size(); i++) {
                Node second = path.get(i);

                renderer.line(first.x + 0.5, first.y + 0.5, first.z + 0.5, second.x + 0.5, second.y + 0.5, second.z + 0.5, pathColor);
                first = second;
            }
        }

        renderer.end();
    }

    public static boolean onKey(int key) {
        return switch (key) {
            case GLFW_KEY_KP_0 -> {
                Voyager.INSTANCE.stop();
                yield true;
            }
            case GLFW_KEY_KP_4 -> {
                BlockPos pos = mc.player.getBlockPos();

                if (start != null && start.equals(pos)) start = null;
                else start = new BlockPos(mc.player.getBlockPos());

                yield true;
            }
            case GLFW_KEY_KP_5 -> {
                BlockPos pos = mc.player.getBlockPos();

                if (end != null && end.equals(pos)) end = null;
                else end = new BlockPos(mc.player.getBlockPos());

                yield true;
            }
            case GLFW_KEY_KP_6 -> {
                findPath();
                yield true;
            }
            case GLFW_KEY_KP_7 -> {
                start = end = null;
                yield true;
            }
            case GLFW_KEY_KP_8 -> {
                Voyager.INSTANCE.getLastPath().clear();
                yield true;
            }
            case GLFW_KEY_KP_9 -> {
                start = end = null;
                Voyager.INSTANCE.getLastPath().clear();
                yield true;
            }
            default -> false;
        };
    }
}
