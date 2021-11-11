package org.meteordev.voyager.utils;

import net.minecraft.util.math.MathHelper;

import static org.meteordev.voyager.Pathfinder.mc;

public class Utils {
    public static double getYaw(double x, double z) {
        return mc.player.getYaw() + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(z - mc.player.getZ(), x - mc.player.getX())) - 90f - mc.player.getYaw());
    }
}
