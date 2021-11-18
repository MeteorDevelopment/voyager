package meteordevelopment.voyager;

import meteordevelopment.voyager.goals.XYZGoal;
import meteordevelopment.voyager.pathfinder.Path;
import meteordevelopment.voyager.settings.Settings;
import meteordevelopment.voyager.utils.Color;
import meteordevelopment.voyager.utils.RenderPath;
import meteordevelopment.voyager.utils.Renderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

import static meteordevelopment.voyager.Voyager.mc;
import static org.lwjgl.glfw.GLFW.*;

public class NoName {
    private static final Renderer renderer = new Renderer();
    private static final Color lineColor = new Color(255, 255, 255, 50);
    private static final Color startColor = new Color(25, 25, 225, 175);
    private static final Color endColor = new Color(225, 25, 25, 175);

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

        Settings settings = Voyager.INSTANCE.getSettings();
        renderer.begin(matrices, false, false);

        if (settings.renderPossibleMoves.get()) {
            MoveGenerator moves = new MoveGenerator(Voyager.INSTANCE.getWorldInterface());

            moves.set(x, y, z);
            while (moves.hasNext()) {
                if (!moves.next()) continue;

                renderer.line(x + 0.5, y + 0.5, z + 0.5, moves.moveX + 0.5, moves.moveY + 0.5, moves.moveZ + 0.5, lineColor);
            }
        }

        if (start != null) renderer.box(start.getX() + 0.4, start.getY() + 0.4, start.getZ() + 0.4, 0.2, startColor);
        if (end != null) renderer.box(end.getX() + 0.4, end.getY() + 0.4, end.getZ() + 0.4, 0.2, endColor);

        if (settings.renderPath.get() == RenderPath.Always || (settings.renderPath.get() == RenderPath.OnlyWhenMoving && Voyager.INSTANCE.isMoving())) {
            if (Voyager.INSTANCE.getLastPath().isValid()) {
                Path.Step step = Voyager.INSTANCE.getLastPath().start();

                while (step.next != null) {
                    Path.Step next = step.next;

                    renderer.line(step.x + 0.5, step.y + 0.5, step.z + 0.5, next.x + 0.5, next.y + 0.5, next.z + 0.5, settings.pathColor.get());
                    step = next;
                }
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
            default -> false;
        };
    }
}
