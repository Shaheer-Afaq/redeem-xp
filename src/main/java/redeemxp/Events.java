package redeemxp;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.permission.Permission;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.command.permission.Permissions;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

import static redeemxp.Config.max_xp;
import static redeemxp.Manager.createXPBottle;

public class Events {
    public static void register(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("redeem")
                    .requires(source -> true)
                    .then(CommandManager.argument("xp", IntegerArgumentType.integer(1))
                        .executes(context -> {
                            int xp = IntegerArgumentType.getInteger(context, "xp");

                            ServerPlayerEntity player = context.getSource().getPlayer();

                            assert player != null;
                            ItemStack stack = player.getMainHandStack();
                            if (stack.getComponents().contains(DataComponentTypes.CUSTOM_DATA)){
                                NbtCompound nbt = stack.getComponents().get(DataComponentTypes.CUSTOM_DATA).copyNbt();
                                if (nbt.contains("xp")){
                                    int storedxp = nbt.getInt("xp").get();

                                }
                            }else{
                                player.getInventory().insertStack(createXPBottle(xp));
                                player.sendMessage(Text.of("Redeemed " + xp + " xp!"), false);
                            }


                            return 1;
                        })
                    )
                    .then(CommandManager.literal("max")
                        .executes(context->{
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            assert player != null;

                            if (player.totalExperience > 0){
                                int xp = Math.min(player.totalExperience, max_xp);
                                player.addExperience(-xp);

                                player.sendMessage(Text.literal("Redeemed " + xp + " xp!").formatted(Formatting.GOLD), false);
                                player.getInventory().insertStack(createXPBottle(xp));
                            }
                            else{
                                player.sendMessage(Text.literal("You don't have any xp!").formatted(Formatting.RED), false);
                            }

                            return 1;
                        }))
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
    }
}
