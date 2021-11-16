package meteordevelopment.voyager.mixin;

import meteordevelopment.voyager.Voyager;
import meteordevelopment.voyager.WorldInterfaceImpl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Nullable public ClientWorld world;

    @Inject(method = "joinWorld", at = @At("TAIL"))
    private void onJoinWorld(CallbackInfo info) {
        Voyager.INSTANCE = new Voyager(new WorldInterfaceImpl(world));
    }
}
