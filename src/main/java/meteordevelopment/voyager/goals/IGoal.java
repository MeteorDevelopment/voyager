package meteordevelopment.voyager.goals;

import meteordevelopment.voyager.pathfinder.Node;

public interface IGoal {
    boolean isInGoal(int x, int y, int z);
    default boolean isInGoal(Node node) { return isInGoal(node.x, node.y, node.z); }

    float heuristic(int x, int y, int z);
    default float heuristic(Node node) { return heuristic(node.x, node.y, node.z); }
}
