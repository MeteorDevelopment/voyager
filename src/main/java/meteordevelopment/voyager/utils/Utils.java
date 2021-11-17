package meteordevelopment.voyager.utils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;

import static meteordevelopment.voyager.Voyager.mc;

public class Utils {
    public static Screen screenToOpen;

    public static double getYaw(double x, double z) {
        return mc.player.getYaw() + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(z - mc.player.getZ(), x - mc.player.getX())) - 90f - mc.player.getYaw());
    }

    public static String getFancyEnumName(Enum<?> value) {
        return value.name().replaceAll("([^_])([A-Z])", "$1 $2");
    }
}
