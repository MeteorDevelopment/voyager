package org.meteordev.voyager;

public record Connection(Type type, Node node) {
    public enum Type {
        Straight,
        CornerBump,
        Jump,
        Jump1
    }

    public float getCost() {
        return switch (type) {
            case Straight -> 1;
            case CornerBump -> 2.5f;
            case Jump, Jump1 -> 3.25f;
        };
    }
}
