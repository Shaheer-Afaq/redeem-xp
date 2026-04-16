package redeemxp.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static redeemxp.Manager.getTotalXp;
import static redeemxp.Manager.updateXPBottle;
import static redeemxp.RedeemXP.CONFIG;

@Mixin(LivingEntity.class)
public class DropExperienceMixin {
    @Inject(method = "dropExperience", at = @At("HEAD"), cancellable = true)
    private void dropExperience(ServerWorld world, Entity attacker, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            if (CONFIG.xp_percentage_on_death() > 0){
                int xp_to_give = getTotalXp(player.experienceLevel, player.experienceProgress) * CONFIG.xp_percentage_on_death()/100;
                ItemStack xp_bottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
                updateXPBottle(xp_bottle, xp_to_give, xp_to_give, "Bottle o' Death");
                player.dropItem(xp_bottle, true, false);
            }
            ci.cancel();
        }
    }
}
