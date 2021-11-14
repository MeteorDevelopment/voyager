package meteordevelopment.voyager;

public record Move(Type type, int x, int y, int z, float cost) {
    public enum Type {
        Forward,
        CornerBump,
        Jump
    }
}
