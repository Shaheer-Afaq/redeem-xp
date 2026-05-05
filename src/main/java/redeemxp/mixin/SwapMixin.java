package redeemxp.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static redeemxp.Manager.*;
import static redeemxp.RedeemXP.CONFIG;

@Mixin(ScreenHandler.class)
public class SwapMixin {
    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    private void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (actionType == SlotActionType.SWAP && button == 40) {
            if (player.getEntityWorld().isClient()) return;
            ItemStack stack = ((ScreenHandler)(Object) this).getSlot(slotIndex).getStack();

            if (stack.getDamage() != 0 && CONFIG.quick_mend_enabled() && quickmend_enabled.getOrDefault(player.getUuid(), false)
                && stack.getOrDefault(DataComponentTypes.ENCHANTMENTS,  ItemEnchantmentsComponent.DEFAULT).getEnchantments().stream().anyMatch(entry -> entry.matchesKey(Enchantments.MENDING))){

                if (stack.isEmpty()) { ci.cancel(); return; }
                int dura_to_add = Math.min(Math.min(stack.getMaxDamage() * CONFIG.quickmend_percentage()/100, stack.getDamage()), getTotalXp(player.experienceLevel, player.experienceProgress) * 2);
                if (dura_to_add != 0) {
                    stack.setDamage(stack.getDamage() - dura_to_add);
                    player.addExperience((int) (-0.5 * dura_to_add * CONFIG.quickmend_penalty()));
                    sendSound(player, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE);
                }
                ci.cancel();

            }
        }
    }
}
