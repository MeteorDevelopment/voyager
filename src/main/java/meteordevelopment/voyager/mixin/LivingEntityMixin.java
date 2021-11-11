package meteordevelopment.voyager.mixin;

import meteordevelopment.voyager.VInput;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "applyMovementInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.BEFORE))
    private void onApplyMovementInput(Vec3d movementInput, float slipperiness, CallbackInfoReturnable<Vec3d> info) {
        if ((Object) this instanceof ClientPlayerEntity player && player.input instanceof VInput input) input.limitMovement(getVelocity());
    }

    @Inject(method = "applyMovementInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.AFTER))
    private void onApplyMovementInput2(Vec3d movementInput, float slipperiness, CallbackInfoReturnable<Vec3d> info) {
        if ((Object) this instanceof ClientPlayerEntity player && player.input instanceof VInput input) input.afterMove();
    }
}
