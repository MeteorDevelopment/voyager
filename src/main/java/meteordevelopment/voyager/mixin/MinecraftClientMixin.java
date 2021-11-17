package meteordevelopment.voyager.mixin;

import meteordevelopment.voyager.Voyager;
import meteordevelopment.voyager.WorldInterfaceImpl;
import meteordevelopment.voyager.commands.Commands;
import meteordevelopment.voyager.utils.Utils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable public ClientWorld world;

    @Shadow public abstract void setScreen(@Nullable Screen screen);

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        Commands.init();
    }

    @Inject(method = "joinWorld", at = @At("TAIL"))
    private void onJoinWorld(CallbackInfo info) {
        Voyager.INSTANCE.setWorldInterface(new WorldInterfaceImpl(world));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        if (Utils.screenToOpen != null) {
            setScreen(Utils.screenToOpen);
            Utils.screenToOpen = null;
        }
    }
}
