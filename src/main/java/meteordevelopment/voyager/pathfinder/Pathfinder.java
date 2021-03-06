package meteordevelopment.voyager.pathfinder;

import it.unimi.dsi.fastutil.PriorityQueue;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectHeapPriorityQueue;
import meteordevelopment.voyager.MoveGenerator;
import meteordevelopment.voyager.Voyager;
import meteordevelopment.voyager.goals.IGoal;
import meteordevelopment.voyager.utils.Chat;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

public class Pathfinder {
    public static Long2ObjectMap<Node> nodes;

    public static Path findPath(Voyager voyager, BlockPos start, IGoal goal) {
        if (voyager.getSettings().chatDebug.get()) Chat.send("Calculating path");

        long startTime = System.nanoTime();

        nodes = new Long2ObjectOpenHashMap<>();
        PriorityQueue<Node> openSet = new ObjectHeapPriorityQueue<>(Comparator.comparingDouble(value -> value.fScore));

        Node startNode = new Node(start.getX(), start.getY(), start.getZ());
        startNode.gScore = 0;
        startNode.fScore = goal.heuristic(startNode);
        nodes.put(BlockPos.asLong(startNode.x, startNode.y, startNode.z), startNode);
        openSet.enqueue(startNode);
        startNode.inOpenSet = true;

        MoveGenerator moves = new MoveGenerator(voyager.getWorldInterface());
        Node endedAt = null;
        int outsideHits = 0;
        boolean shouldContinue = false;

        loop:
        while (openSet.size() > 0) {
            Node current = openSet.dequeue();
            current.inOpenSet = false;

            if (goal.isInGoal(current)) {
                endedAt = current;
                break;
            }

            moves.set(current.x, current.y, current.z);
            while (moves.hasNext()) {
                if (!moves.next()) {
                    if (moves.moveOutside && ++outsideHits >= 50) {
                        endedAt = current;
                        shouldContinue = true;
                        break loop;
                    }
                    continue;
                }

                long key = BlockPos.asLong(moves.moveX, moves.moveY, moves.moveZ);
                Node node = nodes.get(key);

                if (node == null) {
                    node = new Node(moves.moveX, moves.moveY, moves.moveZ);
                    nodes.put(key, node);
                }

                float tentativeGScore = current.gScore + moves.moveCost;

                if (tentativeGScore < node.gScore) {
                    node.cameFrom = current;
                    node.moveType = moves.moveType;

                    node.gScore = tentativeGScore;
                    node.fScore = node.gScore + goal.heuristic(node);

                    if (!node.inOpenSet) {
                        openSet.enqueue(node);
                        node.inOpenSet = true;
                    }
                }
            }
        }

        if (voyager.getSettings().chatDebug.get()) {
            double elapsed = (System.nanoTime() - startTime) / 1000000000.0;
            Chat.send("Finished calculating path in %.3f s" + (endedAt == null ? ", no path" : ""), elapsed);
            Chat.send("  Nodes: %d, Outside hits: %d", nodes.size(), outsideHits);
        }

        Path.Step step = null;

        if (endedAt != null) {
            step = new Path.Step(endedAt.moveType, endedAt.x, endedAt.y, endedAt.z);

            while (endedAt.cameFrom != null) {
                endedAt = endedAt.cameFrom;

                Path.Step next = new Path.Step(endedAt.moveType, endedAt.x, endedAt.y, endedAt.z);
                next.next = step;
                step = next;
            }
        }

        return new Path(voyager, step, shouldContinue ? goal : null);
    }
}
