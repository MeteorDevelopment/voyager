package meteordevelopment.voyager.utils;

import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import static meteordevelopment.voyager.Voyager.mc;

public class Chat {
    private static String format(Formatting color, String format, Object... args) {
        return String.format("%s[%sVoyager%s] %s%s", Formatting.GRAY, Formatting.BLUE, Formatting.GRAY, color, String.format(format, args));
    }

    public static void send(String format, Object... args) {
        mc.player.sendMessage(new LiteralText(format(Formatting.GRAY, format, args)), false);
    }

    public static void error(String format, Object... args) {
        mc.player.sendMessage(new LiteralText(format(Formatting.RED, format, args)), false);
    }
}
