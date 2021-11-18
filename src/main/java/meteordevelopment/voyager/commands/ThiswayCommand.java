package meteordevelopment.voyager.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.voyager.Voyager;
import meteordevelopment.voyager.goals.DirectionGoal;
import meteordevelopment.voyager.goals.XZGoal;
import net.minecraft.command.CommandSource;

import static meteordevelopment.voyager.Voyager.mc;

public class ThiswayCommand extends Command {
    public ThiswayCommand() {
        super("thisway");
    }

    @Override
    protected void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("distance", IntegerArgumentType.integer(1)).executes(context -> {
            int distance = context.getArgument("distance", Integer.class);
            Voyager.INSTANCE.moveTo(new XZGoal(mc.player, distance));

            return SUCCESS;
        }));

        builder.executes(context -> {
            Voyager.INSTANCE.moveTo(new DirectionGoal(mc.player));
            return SUCCESS;
        });
    }
}
