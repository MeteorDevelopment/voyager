package meteordevelopment.voyager;

import java.util.ArrayList;
import java.util.List;

public class Path {
    public static List<Node> simplify(List<Node> oldPath) {
        if (oldPath.size() <= 2) return oldPath;

        List<Node> path = new ArrayList<>(oldPath.size());

        Node start = oldPath.get(0);
        Node last = null;
        int dirX = 0;
        int dirZ = 0;

        path.add(start);

        for (int i = 1; i < oldPath.size(); i++) {
            Node node = oldPath.get(i);

            if (last == null) {
                if (start.y != node.y) {
                    path.add(node);
                    start = node;

                    continue;
                }

                last = node;
                dirX = getDirX(start, last);
                dirZ = getDirZ(start, last);

                continue;
            }

            if (last.y != node.y) {
                path.add(last);
                start = node;
                last = null;

                path.add(start);
                continue;
            }

            if (last.x + dirX == node.x && last.z + dirZ == node.z) {
                last = node;
            }
            else {
                path.add(last);
                start = last;
                last = node;

                dirX = getDirX(start, last);
                dirZ = getDirZ(start, last);
            }
        }

        if (last != null) path.add(last);

        return path;
    }

    public static int getDirX(Node from, Node to) {
        if (from.x == to.x) return 0;
        return to.x > from.x ? 1 : -1;
    }

    public static int getDirZ(Node from, Node to) {
        if (from.z == to.z) return 0;
        return to.z > from.z ? 1 : -1;
    }
}
