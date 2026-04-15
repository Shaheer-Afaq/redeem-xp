package redeemxp;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

import static redeemxp.Config.max_xp;

public class Manager {
    private static MinecraftServer Server;
    private static ServerWorld World;
    public static MinecraftServer getServer(){return Server;}
    public static ServerWorld getWorld(){return World;}

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Server = server;
            World = server.getOverworld();
        });
    }

    public static int redeem(CommandContext<ServerCommandSource> context, int value) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        int redeemable = Math.min(Math.min(value, getTotalXp(player.experienceLevel, player.experienceProgress, player)), max_xp);

        if (redeemable <= 0) {
            player.sendMessage(Text.literal("You don't have enough XP!").formatted(Formatting.RED), false);
            return 0;
        }

        ItemStack stack = player.getMainHandStack();
        boolean handUpdated = false;

        if (stack.contains(DataComponentTypes.CUSTOM_DATA)) {
            NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
            if (customData != null) {
                NbtCompound nbt = customData.copyNbt();
                if (nbt.contains("xp")) {
                    int storedxp = nbt.getInt("xp").get();
                    if (storedxp < max_xp) {
                        int roomInBottle = max_xp - storedxp;
                        int toAdd = Math.min(redeemable, roomInBottle);

                        ItemStack newBottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
                        updateXPBottle(newBottle, storedxp + toAdd);
                        player.setStackInHand(Hand.MAIN_HAND, newBottle);
                        player.addExperience(-toAdd);
                        player.sendMessage(Text.literal("Redeemed " + toAdd + " XP into the current bottle!").formatted(Formatting.GREEN), false);

                        handUpdated = true;
                    }
                }
            }
        }

        if (!handUpdated) {
            ItemStack newBottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
            updateXPBottle(newBottle, redeemable);

            if (!player.getInventory().insertStack(newBottle)) {
                player.dropItem(newBottle, false);
            }
            player.addExperience(-redeemable);
            player.sendMessage(Text.literal("Redeemed " + redeemable + " XP into a new bottle!").formatted(Formatting.GREEN), false);
        }
        return 1;
    }

    public static void updateXPBottle(ItemStack xpbottle, int value){
        new ItemBuilder(xpbottle)
            .setStackSize(1)
            .setName("Bottle o' Enchanting", Formatting.LIGHT_PURPLE)
            .setLore(value + "/" + max_xp + " XP", Formatting.GRAY, true)
            .setNbt(nbt -> nbt.putInt("xp", value))
            .setMaxDura(max_xp).setDura(max_xp - value);
    }
    public static int getTotalXp(int level, float progress, ServerPlayerEntity player) {
        int xpFromLevels;
        if (level <= 16) {
            xpFromLevels = level * level + 6 * level;
        } else if (level <= 31) {
            xpFromLevels = (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            xpFromLevels = (int) (4.5 * level * level - 162.5 * level + 2220);
        }

        int xpFromProgress = Math.round(progress * player.getNextLevelExperience());

        return xpFromLevels + xpFromProgress;
    }
}
