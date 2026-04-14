package redeemxp.mixin;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import redeemxp.access.XPBottleEntityAccess;

import static redeemxp.Manager.getWorld;

@Mixin(ExperienceBottleItem.class)
public class XPBottleItemMixin{
    @Redirect(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/ProjectileEntity;spawnWithVelocity(Lnet/minecraft/entity/projectile/ProjectileEntity$ProjectileCreator;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;FFF)Lnet/minecraft/entity/projectile/ProjectileEntity;"
            )
    )
    private ProjectileEntity setXP(ProjectileEntity.ProjectileCreator<?> creator, ServerWorld world, ItemStack stack, LivingEntity shooter, float roll, float power, float divergence) {
        ExperienceBottleEntity entity = (ExperienceBottleEntity) ProjectileEntity.spawnWithVelocity(creator, world, stack, shooter, roll, power, divergence);
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData != null) {
            NbtCompound nbt = customData.copyNbt();
            if (nbt.contains("xp")) {
                int amount = nbt.getInt("xp").get();
                ((XPBottleEntityAccess) entity).setStoredXp(amount);
            }
        }
        return entity;
    }
}
