package redeemxp;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Formatting;

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

    public static ItemStack createXPBottle(int value){
        return new ItemBuilder(new ItemStack(Items.EXPERIENCE_BOTTLE))
                .setStackSize(1)
                .withNbt(nbt -> {
                    nbt.putInt("xp", value);
                })
                .desc(value + "/" + max_xp + " XP", Formatting.GRAY)
                .name("XP Bottle", Formatting.GOLD)
                .build();
    }
}
