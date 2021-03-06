package meteordevelopment.voyager.mixin;

import meteordevelopment.voyager.VInput;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.voyager.Voyager.mc;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void onChangeLookDirection(double cursorDeltaX, double cursorDeltaY, CallbackInfo info) {
        if (mc.player.input instanceof VInput input) input.changeLookDirection(cursorDeltaX * 0.15);
    }
}
