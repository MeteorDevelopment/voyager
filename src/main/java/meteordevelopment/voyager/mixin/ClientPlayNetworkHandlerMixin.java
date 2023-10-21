package meteordevelopment.voyager.mixin;

import meteordevelopment.voyager.Voyager;
import meteordevelopment.voyager.commands.Commands;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String content, CallbackInfo info) {
        String prefix = Voyager.INSTANCE.getSettings().prefix.get();

        if (content.startsWith(prefix)) {
            Commands.dispatch(content.substring(prefix.length()));
            info.cancel();
        }
    }
}
