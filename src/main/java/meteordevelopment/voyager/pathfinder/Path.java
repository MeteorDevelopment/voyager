package meteordevelopment.voyager.pathfinder;

import meteordevelopment.voyager.Voyager;
import meteordevelopment.voyager.goals.IGoal;

public record Path(Voyager voyager, Step start, IGoal goal) {
    public static class Step {
        public final int x, y, z;
        public Step next;

        public Step(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getDirX(Step to) {
            if (x == to.x) return 0;
            return to.x > x ? 1 : -1;
        }

        public int getDirZ(Step to) {
            if (z == to.z) return 0;
            return to.z > z ? 1 : -1;
        }
    }

    public boolean isValid() {
        return start != null;
    }

    public void continueIfNeeded() {
        if (goal != null) voyager.moveTo(goal);
    }

    /*public static List<Node> simplify(List<Node> oldPath) {
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
    }*/
}
