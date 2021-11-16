package meteordevelopment.voyager;

import meteordevelopment.voyager.goals.IGoal;
import meteordevelopment.voyager.pathfinder.Node;
import meteordevelopment.voyager.pathfinder.Pathfinder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class Voyager {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static Voyager INSTANCE;

    private final IWorldInterface wi;

    public Voyager(IWorldInterface worldInterface) {
        this.wi = worldInterface;
    }

    public IWorldInterface getWorldInterface() {
        return wi;
    }

    public List<Node> findPath(BlockPos start, IGoal goal) {
        return Pathfinder.findPath(wi, start, goal);
    }
}
