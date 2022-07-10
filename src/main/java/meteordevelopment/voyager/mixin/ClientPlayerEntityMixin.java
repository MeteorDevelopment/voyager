package meteordevelopment.voyager.mixin;

import meteordevelopment.voyager.Voyager;
import meteordevelopment.voyager.commands.Commands;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, Text preview, CallbackInfo info) {
        String prefix = Voyager.INSTANCE.getSettings().prefix.get();

        if (message.startsWith(prefix)) {
            Commands.dispatch(message.substring(prefix.length()));
            info.cancel();
        }
    }
}
