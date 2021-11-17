package meteordevelopment.voyager.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.voyager.Voyager;
import meteordevelopment.voyager.utils.Utils;
import net.minecraft.command.CommandSource;

public class SettingsCommand extends Command {
    public SettingsCommand() {
        super("settings");
    }

    @Override
    protected void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            // mc.setScreen() needs to be called after this tick for the screen to actually open
            Utils.screenToOpen = Voyager.INSTANCE.getSettings().createConfigScreen(null);
            return SUCCESS;
        });
    }
}
