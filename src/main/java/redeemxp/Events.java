package redeemxp;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import redeemxp.access.XPBottleEntityAccess;

import static redeemxp.Manager.*;

public class Events {
    public static void register(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("redeem")
                    .requires(source -> true)
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                        .then(CommandManager.literal("xp")
                            .executes(context -> {
                                int amount = IntegerArgumentType.getInteger(context, "amount");
                                return redeem(context, amount);
                            })
                        )
                    )
                    .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                        .then(CommandManager.literal("levels")
                            .executes(context -> {
                                ServerPlayerEntity  player = context.getSource().getPlayer();
                                assert player != null;
                                int amount = IntegerArgumentType.getInteger(context, "amount");
                                int currentLevels = player.experienceLevel;
                                int xp = getTotalXp(currentLevels, player.experienceProgress) - getTotalXp(currentLevels-amount, player.experienceProgress);
                                return redeem(context, xp);
                            })
                        )
                    )
                    .then(CommandManager.literal("max").executes(context-> redeem(context, RedeemXP.CONFIG.max_xp())))
            );
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {

            if (!(world instanceof ServerWorld serverWorld)) {return ActionResult.PASS;}

            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() == Items.EXPERIENCE_BOTTLE) {
                NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
                if (customData != null) {
                    NbtCompound nbt = customData.copyNbt();
                    if (nbt.contains("xp")) {
                        int storedxp = nbt.getInt("xp").get();
                        int max_xp = nbt.getInt("max_xp").get();
                        ItemStack itemStack = player.getStackInHand(hand);

                        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
                        ExperienceBottleEntity entity = ProjectileEntity.spawnWithVelocity(ExperienceBottleEntity::new, serverWorld, itemStack, player, -20.0F, 0.7F, 1.0F);

                        if (player.isSneaking()) {
                            ((XPBottleEntityAccess) entity).setStoredXp(storedxp);
                            stack.decrement(1);
                            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_BREAK.value(), SoundCategory.PLAYERS);
                        } else {
                            int xpToThrow = Math.min(storedxp, RedeemXP.CONFIG.xp_rate());
                            ((XPBottleEntityAccess) entity).setStoredXp(xpToThrow);
                            if (storedxp == xpToThrow) {
                                stack.decrement(1);
                                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_BREAK.value(), SoundCategory.PLAYERS);

                            } else {
                                updateXPBottle(stack, storedxp - xpToThrow, max_xp, "Bottle o' Enchanting");
                            }
                        }
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}
