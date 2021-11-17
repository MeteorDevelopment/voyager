package meteordevelopment.voyager.pathfinder;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import meteordevelopment.voyager.MoveGenerator;
import meteordevelopment.voyager.Voyager;
import meteordevelopment.voyager.goals.IGoal;
import meteordevelopment.voyager.utils.Chat;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Pathfinder {
    public static List<Node> findPath(Voyager voyager, BlockPos start, IGoal goal) {
        if (voyager.getSettings().chatDebug.get()) Chat.send("Calculating path");

        long startTime = System.nanoTime();

        Long2ObjectMap<Node> nodes = new Long2ObjectOpenHashMap<>();
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(value -> value.fScore));

        Node startNode = new Node(start.getX(), start.getY(), start.getZ());
        startNode.gScore = 0;
        startNode.fScore = goal.heuristic(startNode);
        nodes.put(BlockPos.asLong(startNode.x, startNode.y, startNode.z), startNode);
        openSet.enqueue(startNode);

        MoveGenerator moves = new MoveGenerator(voyager.getWorldInterface());
        Node endedAt = null;
        int visited = 0;
        int outsideHits = 0;

        loop:
        while (openSet.size() > 0) {
            Node current = openSet.dequeue();
            visited++;

            if (goal.isInGoal(current)) {
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
                    node.fScore = node.gScore + goal.heuristic(node);

                    if (!openSet.contains(node)) openSet.enqueue(node);
                }
            }
        }

        if (voyager.getSettings().chatDebug.get()) {
            double elapsed = (System.nanoTime() - startTime) / 1000000000.0;
            Chat.send("Finished calculating path in %.3f s" + (endedAt == null ? ", no path" : ""), elapsed);
            Chat.send("  Nodes: %d, Visited: %d, Outside hits: %d", nodes.size(), visited, outsideHits);
        }

        List<Node> path = new ArrayList<>();

        if (endedAt != null) {
            path.add(endedAt);

            while (endedAt.cameFrom != null) {
                endedAt = endedAt.cameFrom;
                path.add(endedAt);
            }

            Collections.reverse(path);
            //path = Path.simplify(path);
        }

        return path;
    }
}
