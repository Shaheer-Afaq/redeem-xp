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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import redeemxp.access.XPBottleEntityAccess;

import static redeemxp.Config.max_xp;
import static redeemxp.Config.xp_amount;
import static redeemxp.Manager.updateXPBottle;
import static redeemxp.Manager.redeem;

public class Events {
    public static void register(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("redeem")
                    .requires(source -> true)
                    .then(CommandManager.argument("xp", IntegerArgumentType.integer(1))
                        .executes(context -> {
                            int value = IntegerArgumentType.getInteger(context, "xp");
                            return redeem(context, value);
                        })
                    )
                    .then(CommandManager.literal("max").executes(context-> redeem(context, max_xp)))
            );
            dispatcher.register(CommandManager.literal("upgrade")
                    .requires(source -> true)
                    .then(CommandManager.argument("xp", IntegerArgumentType.integer(1))
                        .executes(context -> {
//                            ServerCommandSource source = context.getSource();
//                            int value = IntegerArgumentType.getInteger(context, "xp");
//
//                            ServerPlayerEntity player = source.getPlayer();
//                            assert player != null;
//
//                            ItemStack stack = player.getMainHandStack();
//                            if (stack.getItem() == Items.DIAMOND_PICKAXE && !Objects.requireNonNull(stack.get(DataComponentTypes.CUSTOM_DATA)).copyNbt().getBoolean("upgraded").get()){
//                                new ItemBuilder(stack).desc("XP: " + value, Formatting.BLUE)
//                                        .withNbt(nbt->{
//                                            nbt.putBoolean("upgraded", true);
//                                        })
//                                        .withAttribute(EntityAttributes.MINING_EFFICIENCY, 5, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, AttributeModifierSlot.MAINHAND);
//                                player.sendMessage(Text.of("Upgraded " + stack.getName().getString()), false);
//                            }
//

                            return 1;
                        })
                    )
            );
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            if (!(world instanceof ServerWorld serverWorld)) {return ActionResult.SUCCESS;}

            ItemStack stack = player.getStackInHand(hand);
            if (stack.getItem() == Items.EXPERIENCE_BOTTLE){
                NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
                assert customData != null;
                NbtCompound nbt = customData.copyNbt();
                if (nbt.contains("xp")) {
                    int storedxp = nbt.getInt("xp").get();
                    ItemStack itemStack = player.getStackInHand(hand);

                    world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
                    ExperienceBottleEntity entity = ProjectileEntity.spawnWithVelocity(ExperienceBottleEntity::new, serverWorld, itemStack, player, -20.0F, 0.7F, 1.0F);

                    if (player.isSneaking()) {
                        ((XPBottleEntityAccess) entity).setStoredXp(storedxp);
                        stack.decrement(1);
                        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_BREAK.value(), SoundCategory.PLAYERS);
                    } else {
                        int xpToThrow = Math.min(storedxp, xp_amount);
                        ((XPBottleEntityAccess) entity).setStoredXp(xpToThrow);
                        if (storedxp == xpToThrow) {
                            stack.decrement(1);
                            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_BREAK.value(), SoundCategory.PLAYERS);

                        } else {
                            updateXPBottle(stack, storedxp-xpToThrow);
                        }
                    }
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.SUCCESS;
        });
    }
}
