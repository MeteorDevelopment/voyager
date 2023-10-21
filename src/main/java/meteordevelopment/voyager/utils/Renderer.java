package meteordevelopment.voyager.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import static meteordevelopment.voyager.Voyager.mc;

public class Renderer {
    private final BufferBuilder triangles = new BufferBuilder(256 * 256);
    private final BufferBuilder lines = new BufferBuilder(256 * 256);

    private Matrix4f matrix;
    private Vec3d camera;
    private boolean linesOnly, depthTest;

    public void begin(MatrixStack matrices, boolean linesOnly, boolean depthTest) {
        this.matrix = matrices.peek().getPositionMatrix();
        this.linesOnly = linesOnly;
        this.depthTest = depthTest;

        camera = mc.gameRenderer.getCamera().getPos();

        if (!linesOnly) triangles.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        lines.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
    }

    public void box(double x, double y, double z, double s, Color color) {
        // Bottom
        triangles.vertex(matrix, (float) (x - camera.x), (float) (y - camera.y), (float) (z - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x + s - camera.x), (float) (y - camera.y), (float) (z - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x + s - camera.x), (float) (y - camera.y), (float) (z + s - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x - camera.x), (float) (y - camera.y), (float) (z + s - camera.z)).color(color.r, color.g, color.b, color.a).next();

        // Top
        triangles.vertex(matrix, (float) (x - camera.x), (float) (y + s - camera.y), (float) (z - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x - camera.x), (float) (y + s - camera.y), (float) (z + s - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x + s - camera.x), (float) (y + s - camera.y), (float) (z + s - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x + s - camera.x), (float) (y + s - camera.y), (float) (z - camera.z)).color(color.r, color.g, color.b, color.a).next();

        // Back
        triangles.vertex(matrix, (float) (x - camera.x), (float) (y - camera.y), (float) (z - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x - camera.x), (float) (y - camera.y + s), (float) (z - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x - camera.x + s), (float) (y - camera.y + s), (float) (z - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x - camera.x + s), (float) (y - camera.y), (float) (z - camera.z)).color(color.r, color.g, color.b, color.a).next();

        // Front
        triangles.vertex(matrix, (float) (x - camera.x), (float) (y - camera.y), (float) (z + s - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x - camera.x + s), (float) (y - camera.y), (float) (z + s - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x - camera.x + s), (float) (y - camera.y + s), (float) (z + s - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x - camera.x), (float) (y - camera.y + s), (float) (z + s - camera.z)).color(color.r, color.g, color.b, color.a).next();

        // Left
        triangles.vertex(matrix, (float) (x - camera.x), (float) (y - camera.y), (float) (z - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x - camera.x), (float) (y - camera.y), (float) (z + s - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x - camera.x), (float) (y + s - camera.y), (float) (z + s - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x - camera.x), (float) (y + s - camera.y), (float) (z - camera.z)).color(color.r, color.g, color.b, color.a).next();

        // Right
        triangles.vertex(matrix, (float) (x + s - camera.x), (float) (y - camera.y), (float) (z - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x + s - camera.x), (float) (y + s - camera.y), (float) (z - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x + s - camera.x), (float) (y + s - camera.y), (float) (z + s - camera.z)).color(color.r, color.g, color.b, color.a).next();
        triangles.vertex(matrix, (float) (x + s - camera.x), (float) (y - camera.y), (float) (z + s - camera.z)).color(color.r, color.g, color.b, color.a).next();
    }

    public void line(double x1, double y1, double z1, double x2, double y2, double z2, Color color) {
        lines.vertex(matrix, (float) (x1 - camera.x), (float) (y1 - camera.y), (float) (z1 - camera.z)).color(color.r, color.g, color.b, color.a).next();
        lines.vertex(matrix, (float) (x2 - camera.x), (float) (y2 - camera.y), (float) (z2 - camera.z)).color(color.r, color.g, color.b, color.a).next();
    }

    public void end() {
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        if (depthTest) RenderSystem.enableDepthTest();
        else RenderSystem.disableDepthTest();

        BufferRenderer.drawWithGlobalProgram(lines.end());
        if (!linesOnly) BufferRenderer.drawWithGlobalProgram(triangles.end());

        RenderSystem.enableDepthTest();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
    }
}
