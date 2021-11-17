package meteordevelopment.voyager.utils;

public enum RenderPath {
    Never,
    OnlyWhenMoving,
    Always;

    @Override
    public String toString() {
        return Utils.getFancyEnumName(this);
    }
}
