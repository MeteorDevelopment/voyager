package org.meteordev.voyager.utils;

import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import static org.meteordev.voyager.Pathfinder.mc;

public class Chat {
    public static void send(String format, Object... args) {
        mc.player.sendMessage(new LiteralText(String.format("%s[%sVoyager%s] %s", Formatting.GRAY, Formatting.BLUE, Formatting.GRAY, String.format(format, args))), false);
    }
}
