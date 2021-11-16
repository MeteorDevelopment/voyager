package meteordevelopment.voyager.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Collection;
import java.util.List;

public class MyBlockPosArgument {
    public final int x, y, z;
    public final boolean hasY;

    public MyBlockPosArgument(int x, int y, int z, boolean hasY) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hasY = hasY;
    }

    public static Type blockPos() {
        return new Type();
    }

    public static class Type implements ArgumentType<MyBlockPosArgument> {
        @Override
        public MyBlockPosArgument parse(StringReader reader) throws CommandSyntaxException {
            int x;
            int y = 0;
            int z;
            boolean hasY = false;

            x = reader.readInt();
            reader.expect(' ');
            z = reader.readInt();

            if (reader.canRead() && reader.peek() == ' ') {
                try {
                    reader.expect(' ');
                    int temp = reader.readInt();
                    y = z;
                    z = temp;
                    hasY = true;
                } catch (CommandSyntaxException ignored) {}
            }

            return new MyBlockPosArgument(x, y, z, hasY);
        }

        @Override
        public Collection<String> getExamples() {
            return List.of("5 36", "5 70 36");
        }
    }
}
