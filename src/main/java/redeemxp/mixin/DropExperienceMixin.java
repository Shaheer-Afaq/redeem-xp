package redeemxp.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static redeemxp.Manager.*;
import static redeemxp.RedeemXP.CONFIG;

@Mixin(LivingEntity.class)
public class DropExperienceMixin {
    @Inject(method = "dropExperience", at = @At("HEAD"), cancellable = true)
    private void dropExperience(ServerWorld world, Entity attacker, CallbackInfo ci) {
        if ((Object) this instanceof ServerPlayerEntity player) {
            if (CONFIG.xp_percentage_on_death() > 0){
                int xp_to_give = getTotalXp(player.experienceLevel, player.experienceProgress) * CONFIG.xp_percentage_on_death()/100;
                ItemStack xp_bottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
                updateXPBottle(xp_bottle, xp_to_give, xp_to_give, "");
                xp_bottle.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Experience of Death").formatted(Formatting.RED));
                player.getEntityWorld().spawnEntity(new ItemEntity(player.getEntityWorld(), player.getX(), player.getY(), player.getZ(), xp_bottle));
            }
            ci.cancel();
        }
    }
}
