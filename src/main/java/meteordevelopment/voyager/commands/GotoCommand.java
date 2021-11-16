package meteordevelopment.voyager.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.voyager.Voyager;
import meteordevelopment.voyager.goals.XYZGoal;
import meteordevelopment.voyager.goals.XZGoal;
import net.minecraft.command.CommandSource;

public class GotoCommand extends Command {
    public GotoCommand() {
        super("goto");
    }

    @Override
    protected void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("pos", MyBlockPosArgument.blockPos()).executes(context -> {
            MyBlockPosArgument pos = context.getArgument("pos", MyBlockPosArgument.class);

            if (pos.hasY) Voyager.INSTANCE.moveTo(new XYZGoal(pos.x, pos.y, pos.z));
            else Voyager.INSTANCE.moveTo(new XZGoal(pos.x, pos.z));

            return SUCCESS;
        }));
    }
}
