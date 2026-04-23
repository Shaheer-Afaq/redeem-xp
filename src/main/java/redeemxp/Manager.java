package redeemxp;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import static redeemxp.RedeemXP.MOD_ID;

public class Manager {
    private static MinecraftServer Server;
    public static MinecraftServer getServer(){return Server;}

    public static final AttachmentType<Integer> ENTITY_STOREDXP = AttachmentRegistry.create(Identifier.of(MOD_ID, "stored_xp"));

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Server = server;
        });
    }

    public static int redeem(CommandContext<ServerCommandSource> context, int value) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        ItemStack stack = player.getMainHandStack();
        boolean handUpdated = false;

        if (getTotalXp(player.experienceLevel, player.experienceProgress) <= 0) {
            player.sendMessage(Text.literal("You don't have enough XP!").formatted(Formatting.RED), false);
            return 0;
        }

        int redeemable = Math.min(value, getTotalXp(player.experienceLevel, player.experienceProgress));

        if (stack.contains(DataComponentTypes.CUSTOM_DATA)) {
            NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
            if (customData != null) {
                NbtCompound nbt = customData.copyNbt();
                if (nbt.contains("xp")) {
                    int storedxp = nbt.getInt("xp").get();
                    int maxxp = nbt.getInt("max_xp").get();
//                    redeemable = Math.min(redeemable, maxxp-storedxp);

                    if (storedxp < maxxp) {
                        int roomInBottle = maxxp - storedxp;
                        int toAdd = Math.min(redeemable, roomInBottle);
                        updateXPBottle(stack, storedxp + toAdd, maxxp, "Bottle o' Enchanting");
                        player.addExperience(-toAdd);
                        player.sendMessage(Text.literal("Redeemed " + toAdd + " XP into the current bottle!").formatted(Formatting.GREEN), false);

                        handUpdated = true;
                    }
                }
            }
        }

        if (!handUpdated) {
            ItemStack newBottle = new ItemStack(Items.EXPERIENCE_BOTTLE);
            updateXPBottle(newBottle, redeemable, RedeemXP.CONFIG.max_xp(), "Bottle o' Enchanting");

            if (!player.getInventory().insertStack(newBottle)) {
                player.dropItem(newBottle, false);
            }
            player.addExperience(-redeemable);
            player.sendMessage(Text.literal("Redeemed " + redeemable + " XP into a new bottle!").formatted(Formatting.GREEN), false);
        }
        return 1;
    }

    public static void updateXPBottle(ItemStack xpbottle, int xp, int max_xp, String name){
        new ItemBuilder(xpbottle)
            .setStackSize(1).setName(name, Formatting.LIGHT_PURPLE)
            .setLore(xp + "/" + max_xp + " XP", Formatting.GRAY, true)
            .setNbt(nbt -> nbt.putInt("xp", xp))
            .setNbt(nbt -> nbt.putInt("max_xp", max_xp))
            .setMaxDura(max_xp).setDura(max_xp - xp);
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
