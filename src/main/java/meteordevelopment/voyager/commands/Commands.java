package meteordevelopment.voyager.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import meteordevelopment.voyager.utils.Chat;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;

import static meteordevelopment.voyager.Voyager.mc;

public class Commands {
    public static final CommandDispatcher<CommandSource> DISPATCHER = new CommandDispatcher<>();
    public static final CommandSource COMMAND_SOURCE = new ClientCommandSource(null, mc);

    public static void init() {
        add(new SettingsCommand());
        add(new GotoCommand());
        add(new StopCommand());
    }

    private static void add(Command command) {
        command.registerTo(DISPATCHER);
    }

    public static void dispatch(String message) {
        ParseResults<CommandSource> results = DISPATCHER.parse(message, new ClientCommandSource(null, mc));

        try {
            DISPATCHER.execute(results);
        } catch (CommandSyntaxException e) {
            Chat.error(e.getMessage());
        }
    }
}
