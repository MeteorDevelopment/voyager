package meteordevelopment.voyager.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.voyager.Voyager;
import net.minecraft.command.CommandSource;

public class StopCommand extends Command {
    public StopCommand() {
        super("stop");
    }

    @Override
    protected void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            Voyager.INSTANCE.stop();
            return SUCCESS;
        });
    }
}
