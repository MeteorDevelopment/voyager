package meteordevelopment.voyager;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import meteordevelopment.voyager.utils.Chat;
import meteordevelopment.voyager.utils.Color;
import meteordevelopment.voyager.utils.Renderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Voyager {
    public static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final Renderer renderer = new Renderer();
    private static final Color lineColor = new Color(255, 255, 255, 50);
    private static final Color startColor = new Color(25, 25, 225, 175);
    private static final Color endColor = new Color(225, 25, 25, 175);
    private static final Color pathColor = new Color(25, 225, 25, 255);

    private static Context ctx;

    private static BlockPos start, end;

    private static final List<Node> path = new ArrayList<>();
    private static Input prevInput;

    public static void init() {
        ctx = new Context(mc.world);
    }

    private static void findPath() {
        if (end == null) return;

        BlockPos start = Voyager.start;
        boolean startMoving = false;

        if (start == null) {
            start = mc.player.getBlockPos();
            if (start == null) return;

            startMoving = true;
        }

        Chat.send("Calculating path");
        stopMovement();

        long startTime = System.nanoTime();

        Long2ObjectMap<Node> nodes = new Long2ObjectOpenHashMap<>();
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(value -> value.fScore));

        Node startNode = new Node(start.getX(), start.getY(), start.getZ());
        startNode.gScore = 0;
        startNode.fScore = h(startNode);
        nodes.put(BlockPos.asLong(startNode.x, startNode.y, startNode.z), startNode);
        openSet.enqueue(startNode);

        MoveGenerator moves = new MoveGenerator(ctx);
        Node endedAt = null;
        int visited = 0;
        int outsideHits = 0;

        loop:
        while (openSet.size() > 0) {
            Node current = openSet.dequeue();
            visited++;

            if (current.x == end.getX() && current.y == end.getY() && current.z == end.getZ()) {
                endedAt = current;
                break;
            }

            moves.set(current.x, current.y, current.z);
            while (moves.hasNext()) {
                if (!moves.next()) {
                    if (moves.moveOutside && ++outsideHits >= 50) {
                        endedAt = current;
                        break loop;
                    }
                    continue;
                }

                long key = BlockPos.asLong(moves.moveX, moves.moveY, moves.moveZ);

                Node node = nodes.get(key);
                if (node != null) continue;

                node = new Node(moves.moveX, moves.moveY, moves.moveZ);
                nodes.put(key, node);

                float tentativeGScore = current.gScore + moves.moveCost;

                if (tentativeGScore < node.gScore) {
                    node.cameFrom = current;

                    node.gScore = tentativeGScore;
                    node.fScore = node.gScore + h(node);

                    if (!openSet.contains(node)) openSet.enqueue(node);
                }
            }
        }

        double elapsed = (System.nanoTime() - startTime) / 1000000000.0;
        Chat.send("Finished calculating path in %.3f s" + (endedAt == null ? ", no path" : ""), elapsed);
        Chat.send("  Nodes: %d, Visited: %d, Outside hits: %d", nodes.size(), visited, outsideHits);

        if (endedAt == null) return;

        path.clear();
        path.add(endedAt);

        while (endedAt.cameFrom != null) {
            endedAt = endedAt.cameFrom;
            path.add(endedAt);
        }

        Collections.reverse(path);
        //path = Path.simplify(path);

        if (startMoving) {
            prevInput = mc.player.input;
            mc.player.input = new VInput(path);
        }
    }

    private static float h(Node node) {
        float x = end.getX() + 0.5f - node.x;
        float y = end.getY() + 0.5f - node.y;
        float z = end.getZ() + 0.5f - node.z;
        //return x * x + y * y + z * z;
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public static void render(MatrixStack matrices) {
        int x = mc.player.getBlockX();
        int y = mc.player.getBlockY();
        int z = mc.player.getBlockZ();

        MoveGenerator moves = new MoveGenerator(ctx);
        renderer.begin(matrices, false, false);

        moves.set(x, y, z);
        while (moves.hasNext()) {
            if (!moves.next()) continue;

            renderer.line(x + 0.5, y + 0.5, z + 0.5, moves.moveX + 0.5, moves.moveY + 0.5, moves.moveZ + 0.5, lineColor);
        }

        if (start != null) renderer.box(start.getX() + 0.4, start.getY() + 0.4, start.getZ() + 0.4, 0.2, startColor);
        if (end != null) renderer.box(end.getX() + 0.4, end.getY() + 0.4, end.getZ() + 0.4, 0.2, endColor);

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

    public static void stopMovement() {
        if (prevInput != null) {
            mc.player.input = prevInput;
            prevInput = null;
        }
    }

    public static boolean onKey(int key) {
        return switch (key) {
            case GLFW_KEY_KP_0 -> {
                stopMovement();
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
                path.clear();
                yield true;
            }
            case GLFW_KEY_KP_9 -> {
                start = end = null;
                path.clear();
                yield true;
            }
            default -> false;
        };
    }
}
