package meteordevelopment.voyager;

import net.minecraft.block.BlockState;

public interface IWorldInterface {
    BlockState getBlockState(int x, int y, int z);

    boolean isOutside(int x, int z);
}
