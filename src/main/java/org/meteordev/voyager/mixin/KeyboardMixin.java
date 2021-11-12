package org.meteordev.voyager.mixin;

import org.meteordev.voyager.Pathfinder;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.meteordev.voyager.Pathfinder.mc;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo info) {
        if (action == GLFW_PRESS && mc.currentScreen == null) {
            if (Pathfinder.onKey(key)) info.cancel();
        }
    }
}