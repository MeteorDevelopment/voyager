package org.meteordev.voyager;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import org.meteordev.voyager.utils.Chat;
import org.meteordev.voyager.utils.Color;
import org.meteordev.voyager.utils.Renderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import java.util.*;

public class Pathfinder {
    public static final MinecraftClient mc = MinecraftClient.getInstance();

    private static final Color sideColor = new Color(255, 255, 255, 50);
    private static final Color lineColor = new Color(255, 255, 255, 50);
    private static final Color startColor = new Color(25, 25, 225, 175);
    private static final Color endColor = new Color(225, 25, 25, 175);
    private static final Color pathColor = new Color(25, 225, 25, 255);

    private static final Renderer renderer = new Renderer();

    public static Grid grid;
    private static Node start, end;
    private static List<Node> path;
    private static Input prevInput;

    private static void generateGrid() {
        Chat.send("Generating grid");

        grid = new Grid(mc.player.getChunkPos());

        refreshStartAndEndNodes();
    }

    private static void refreshStartAndEndNodes() {
        if (start != null) setNode(true, start.x(), start.y(), start.z(), false);
        if (end != null) setNode(false, end.x(), end.y(), end.z(), false);
    }

    private static class PriorityQueueImpl<T> extends ObjectHeapPriorityQueue<T> {
        public PriorityQueueImpl(Comparator<? super T> c) {
            super(c);
        }

        public boolean contains(T v) {
            for (int i = 0; i < size; i++) {
                if (heap[i] == v) return true;
            }

            return false;
        }
    }

    private static void calculatePath() {
        if (grid == null || end == null) return;

        long startTime = System.nanoTime();

        if (grid.updateChunks()) refreshStartAndEndNodes();
        if (end == null) return;

        Node start = Pathfinder.start;
        boolean move = false;

        if (start == null) {
            BlockPos pos = mc.player.getBlockPos();

            start = grid.getNode(pos.getX(), pos.getY(), pos.getZ());
            if (start == null) return;

            move = true;
        }

        Chat.send("Calculating path");
        stopMovement();

        Object2FloatMap<Node> gScore = new Object2FloatOpenHashMap<>();
        gScore.defaultReturnValue(Float.POSITIVE_INFINITY);
        gScore.put(start, 0);

        Object2FloatMap<Node> fScore = new Object2FloatOpenHashMap<>();
        fScore.defaultReturnValue(Float.POSITIVE_INFINITY);
        fScore.put(start, h(start));

        PriorityQueueImpl<Node> openSet = new PriorityQueueImpl<>(Comparator.comparingDouble(fScore));
        openSet.enqueue(start);

        Map<Node, Node> cameFrom = new HashMap<>();
        Node endedAt = null;
        int visited = 0;

        while (openSet.size() > 0) {
            Node current = openSet.dequeue();
            visited++;

            if (current == end) {
                endedAt = current;
                break;
            }

            for (Connection c : current.connections()) {
                Node n = c.node();
                float tentativeGScore = gScore.getFloat(current) + c.getCost();

                if (tentativeGScore < gScore.getFloat(n)) {
                    cameFrom.put(n, current);

                    gScore.put(n, tentativeGScore);
                    fScore.put(n, gScore.getFloat(n) + h(n));

                    if (!openSet.contains(n)) openSet.enqueue(n);
                }
            }
        }

        double elapsed = (System.nanoTime() - startTime) / 1000000000.0;
        Chat.send("Finished calculating path in %.3f s" + (endedAt == null ? ", no path" : ""), elapsed);
        Chat.send("  Nodes: %d, Visited: %d", grid.nodeCount, visited);

        if (endedAt == null) return;

        path = new ArrayList<>();
        path.add(endedAt);

        while (cameFrom.containsKey(endedAt)) {
            endedAt = cameFrom.get(endedAt);
            path.add(endedAt);
        }

        Collections.reverse(path);
        //path = Path.simplify(path);

        if (move) {
            prevInput = mc.player.input;
            mc.player.input = new VInput(path);
        }
    }

    private static float h(Node node) {
        return end.distanceTo(node);
    }

    private static void setNode(boolean start, int x, int y, int z, boolean toggle) {
        if (grid == null) return;

        Node node = grid.getNode(x, y, z);
        if (node == null) return;

        if (toggle) {
            if (start) Pathfinder.start = Pathfinder.start == node ? null : node;
            else end = end == node ? null : node;
        }
        else {
            if (start) Pathfinder.start = node;
            else end = node;
        }
    }

    public static void stopMovement() {
        if (prevInput != null) {
            mc.player.input = prevInput;
            prevInput = null;
        }
    }

    public static void render(MatrixStack matrices) {
        // Render grid
        if (grid != null) {
            renderer.begin(matrices, false, true);

            for (int x = mc.player.getChunkPos().x - 1; x <= mc.player.getChunkPos().x + 1; x++) {
                for (int z = mc.player.getChunkPos().z - 1; z <= mc.player.getChunkPos().z + 1; z++) {
                    VChunk chunk = grid.getChunk(x, z);
                    if (chunk == null) continue;

                    for (Node node : chunk) {
                        if (node != start && node != end) {
                            renderer.box(node.x() + 0.4, node.y() + 0.4, node.z() + 0.4, 0.2, sideColor);
                        }

                        for (Connection c : node.connections()) {
                            line(node, c.node(), lineColor);
                        }
                    }
                }
            }

            if (start != null) renderer.box(start.x() + 0.4, start.y() + 0.4, start.z() + 0.4, 0.2, startColor);
            if (end != null) renderer.box(end.x() + 0.4, end.y() + 0.4, end.z() + 0.4, 0.2, endColor);

            renderer.end();
        }

        // Render path
        if (path != null && path.size() > 1) {
            renderer.begin(matrices, true, false);

            Node first = path.get(0);

            for (int i = 1; i < path.size(); i++) {
                Node second = path.get(i);

                line(first, second, pathColor);
                first = second;
            }

            renderer.end();
        }
    }

    private static void line(Node from, Node to, Color color) {
        renderer.line(from.x() + 0.5, from.y() + 0.5, from.z() + 0.5, to.x() + 0.5, to.y() + 0.5, to.z() + 0.5, color);
    }

    public static boolean onKey(int key) {
        return switch (key) {
            case GLFW.GLFW_KEY_KP_7 -> {
                generateGrid();
                yield true;
            }
            case GLFW.GLFW_KEY_KP_8 -> {
                grid = null;
                start = end = null;
                path = null;
                yield true;
            }
            case GLFW.GLFW_KEY_KP_4 -> {
                BlockPos pos = mc.player.getBlockPos();
                setNode(true, pos.getX(), pos.getY(), pos.getZ(), true);
                yield true;
            }
            case GLFW.GLFW_KEY_KP_5 -> {
                BlockPos pos = mc.player.getBlockPos();
                setNode(false, pos.getX(), pos.getY(), pos.getZ(), true);
                yield true;
            }
            case GLFW.GLFW_KEY_KP_6 -> {
                calculatePath();
                yield true;
            }
            case GLFW.GLFW_KEY_KP_0 -> {
                stopMovement();
                yield true;
            }
            default -> false;
        };
    }
}
