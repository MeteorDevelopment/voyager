package meteordevelopment.voyager;

import meteordevelopment.voyager.goals.IGoal;
import meteordevelopment.voyager.pathfinder.Node;
import meteordevelopment.voyager.pathfinder.Pathfinder;
import meteordevelopment.voyager.settings.Settings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Voyager {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final Voyager INSTANCE = new Voyager();

    private final Settings settings = new Settings(new File(FabricLoader.getInstance().getConfigDir().toFile(), "voyager.json"));
    private IWorldInterface wi;

    private List<Node> lastPath = new ArrayList<>();
    private Input prevInput;

    public Settings getSettings() {
        return settings;
    }

    public IWorldInterface getWorldInterface() {
        return wi;
    }
    public void setWorldInterface(IWorldInterface wi) {
        this.wi = wi;
    }

    public List<Node> getLastPath() {
        return lastPath;
    }

    public List<Node> findPath(BlockPos start, IGoal goal) {
        lastPath = Pathfinder.findPath(this, start, goal);
        return lastPath;
    }

    public void moveTo(IGoal goal) {
        stop();

        findPath(mc.player.getBlockPos(), goal);

        if (lastPath.size() > 0) {
            prevInput = mc.player.input;
            mc.player.input = new VInput(lastPath);
        }
    }

    public void stop() {
        if (isMoving()) {
            mc.player.input = prevInput;
            prevInput = null;
        }
    }

    public boolean isMoving() {
        return prevInput != null;
    }
}
