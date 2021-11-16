package meteordevelopment.voyager.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.CommandSource;

public abstract class Command {
    private final String name;

    public Command(String name) {
        this.name = name;
    }

    protected abstract void build(LiteralArgumentBuilder<CommandSource> builder);

    public void registerTo(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(name);
        build(builder);
        dispatcher.register(builder);
    }

    protected static final int SUCCESS = com.mojang.brigadier.Command.SINGLE_SUCCESS;

    protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }
}
