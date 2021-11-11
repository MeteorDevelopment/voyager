package org.meteordev.voyager;

import java.util.List;

public record Node(int x, int y, int z, List<Connection> connections) {
    public float distanceTo(Node node) {
        float dX = node.x - x;
        float dY = node.y - y;
        float dZ = node.z - z;
        return (float) Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (x != node.x) return false;
        if (y != node.y) return false;
        return z == node.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
