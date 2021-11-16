package meteordevelopment.voyager.mixin;

import meteordevelopment.voyager.NoName;
import meteordevelopment.voyager.VInput;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.voyager.Voyager.mc;

@Mixin(value = GameRenderer.class, priority = 100)
public abstract class GameRendererMixin {
    @Shadow public abstract void updateTargetedEntity(float tickDelta);

    @Unique private boolean skip;

    @Inject(method = "renderWorld", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = { "ldc=hand" }))
    private void onRenderWorld(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo info) {
        NoName.render(matrix);
    }

    @Inject(method = "updateTargetedEntity", at = @At("HEAD"), cancellable = true)
    private void updateTargetedEntityInvoke(float tickDelta, CallbackInfo info) {
        if (mc.player != null && mc.player.input instanceof VInput input && !skip) {
            info.cancel();
            Entity cameraE = mc.getCameraEntity();

            float yaw = cameraE.getYaw();
            float prevYaw = cameraE.prevYaw;

            cameraE.setYaw(input.getYaw(tickDelta));

            skip = true;
            updateTargetedEntity(tickDelta);
            skip = false;

            cameraE.setYaw(yaw);
            cameraE.prevYaw = prevYaw;
        }
    }
}
