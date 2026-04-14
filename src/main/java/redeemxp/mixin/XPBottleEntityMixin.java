package redeemxp.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redeemxp.access.XPBottleEntityAccess;

import static redeemxp.Manager.getWorld;

@Mixin(ExperienceBottleEntity.class)
public abstract class XPBottleEntityMixin extends Entity implements XPBottleEntityAccess {

    @Unique
    private int storedXp = 0;

    @Override
    public void setStoredXp(int amount) {
        this.storedXp = amount;
    }

    @Override
    public int getStoredXp() {
        return this.storedXp;
    }

    public XPBottleEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    private void onCollision(HitResult hitResult, CallbackInfo ci) {
        if (this.getEntityWorld() instanceof ServerWorld serverWorld && this.storedXp > 0) {
            serverWorld.syncWorldEvent(2002, this.getBlockPos(), -13083194);
            ExperienceOrbEntity.spawn(serverWorld, this.getEntityPos(), getStoredXp());
            this.discard();
            ci.cancel();
        }
    }
}