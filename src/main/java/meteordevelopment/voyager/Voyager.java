package meteordevelopment.voyager;

import meteordevelopment.voyager.goals.IGoal;
import meteordevelopment.voyager.pathfinder.Node;
import meteordevelopment.voyager.pathfinder.Pathfinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class Voyager {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static Voyager INSTANCE;

    private final IWorldInterface wi;

    private List<Node> lastPath = new ArrayList<>();
    private Input prevInput;

    public Voyager(IWorldInterface worldInterface) {
        this.wi = worldInterface;
    }

    public IWorldInterface getWorldInterface() {
        return wi;
    }

    public List<Node> getLastPath() {
        return lastPath;
    }

    public List<Node> findPath(BlockPos start, IGoal goal) {
        lastPath = Pathfinder.findPath(wi, start, goal);
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
