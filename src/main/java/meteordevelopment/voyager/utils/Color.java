package meteordevelopment.voyager.utils;

public class Color {
    public int r, g, b, a;

    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public Color(int packed) {
        this(packed >> 16 & 0xFF, packed >> 8 & 0xFF, packed & 0xFF, packed >> 24 & 0xFF);
    }

    public int pack() {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}
