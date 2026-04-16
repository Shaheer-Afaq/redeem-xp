package redeemxp;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;

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
        int redeemable = Math.min(Math.min(value, getTotalXp(player.experienceLevel, player.experienceProgress)), RedeemXP.CONFIG.max_xp());

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
                    if (storedxp < RedeemXP.CONFIG.max_xp()) {
                        int roomInBottle = RedeemXP.CONFIG.max_xp() - storedxp;
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
            .setLore(value + "/" + RedeemXP.CONFIG.max_xp() + " XP", Formatting.GRAY, true)
            .setNbt(nbt -> nbt.putInt("xp", value))
            .setMaxDura(RedeemXP.CONFIG.max_xp()).setDura(RedeemXP.CONFIG.max_xp() - value)
            .setComponent(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
    }
    public static int getTotalXp(int level, float progress) {
        long xpFromLevels;
        if (level <= 16) {
            xpFromLevels = (long) level * level + 6L * level;
        } else if (level <= 31) {
            xpFromLevels = (long) (2.5 * level * level - 40.5 * level + 360);
        } else {
            xpFromLevels = (long) (4.5 * level * level - 162.5 * level + 2220);
        }
        int nextLevelExperience;
        if (level >= 30) {
            nextLevelExperience =  112 + (level - 30) * 9;
        } else {
            nextLevelExperience =  level >= 15 ? 37 + (level - 15) * 5 : 7 + level * 2;
        }
        int xpFromProgress = (int) (progress * nextLevelExperience);

        return (int) Math.min(xpFromLevels + xpFromProgress, Integer.MAX_VALUE);
    }
}
