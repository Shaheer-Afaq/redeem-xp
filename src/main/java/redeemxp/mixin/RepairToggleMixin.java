package redeemxp.mixin;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static redeemxp.Manager.repair_enabled;

@Mixin(ExperienceOrbEntity.class)
public class RepairToggleMixin {

    @Inject(method = "repairPlayerGears", at = @At("HEAD"), cancellable = true)
    private void repair(ServerPlayerEntity player, int amount, CallbackInfoReturnable<Integer> cir){
        if (!repair_enabled.getOrDefault(player.getUuid(), true)) {
            cir.setReturnValue(amount);
        }
    }
}
