package net.tntchina.fakeplayer;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Utils {

    public static ServerPlayer getServerPlayerByName(String name, ServerLevel world) {
        for (ServerPlayer player : world.players()) {
            Component component = player.getName();
            String n = component.toString();

            if (n.equals("literal{" + name + "}")) {
                return player;
            }
        }

        return null;
    }

    public static boolean isFakePlayerExisting(String name) {
        for (World world : Bukkit.getWorlds()) {
            CraftWorld world2 = (CraftWorld) world;
            ServerLevel nmsWorld = world2.getHandle();

            for (Entity entity : nmsWorld.getEntities().getAll()) {
                if (!(entity instanceof ServerPlayer)) {
                    continue;
                }

                ServerPlayer player2 = (ServerPlayer) entity;

                if (player2.getName().toString().contains(Utils.getPrefix())) {
                    String name_1 = player2.displayName;
                    String name_2 = name_1.substring(name_1.indexOf(ChatColor.AQUA + "") + 1);

                    if (name_2.equals(name)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static ServerPlayer getFakePlayer(String name) {
        for (World world : Bukkit.getWorlds()) {
            CraftWorld world2 = (CraftWorld) world;
            ServerLevel nmsWorld = world2.getHandle();

            for (Entity entity : nmsWorld.getEntities().getAll()) {
                if (!(entity instanceof ServerPlayer)) {
                    continue;
                }

                ServerPlayer player2 = (ServerPlayer) entity;

                if (player2.getName().toString().contains(Utils.getPrefix())) {
                    String name_1 = player2.displayName;
                    String name_2 = name_1.substring(name_1.indexOf(ChatColor.AQUA + "") + 1);

                    if (name_2.equals(name)) {
                        return player2;
                    }
                }
            }
        }

        return null;
    }

    public static CraftWorld getWorldByPlayer(ServerPlayer player) {
        for (World world : Bukkit.getWorlds()) {
            CraftWorld world2 = (CraftWorld) world;
            ServerLevel nmsWorld = world2.getHandle();

            for (Entity entity : nmsWorld.getEntities().getAll()) {
                if (!(entity instanceof ServerPlayer)) {
                    continue;
                }

                ServerPlayer player2 = (ServerPlayer) entity;

                if (player.equals(player2)) {
                    return (CraftWorld) world;
                }
            }
        }

        return null;
    }

    public static List<ServerPlayer> getFakePlayersList() {
        List<ServerPlayer> list = new ArrayList<>();

        for(World world : Bukkit.getWorlds()) {
            CraftWorld craftWorld = (CraftWorld) world;
            ServerLevel nmsWorld = craftWorld.getHandle();

            for (Entity entity : nmsWorld.getEntities().getAll()) {
                if (!(entity instanceof ServerPlayer)) {
                    continue;
                }

                ServerPlayer player2 = (ServerPlayer) entity;

                if (player2.getName().toString().contains(Utils.getPrefix())) {
                    list.add(player2);
                }
            }
        }

        return list;
    }

    public static Map<ServerPlayer, CraftWorld> getFakePlayerMaps() {
        Map<ServerPlayer, CraftWorld> playerMaps = new HashMap<>();
        for(World world : Bukkit.getWorlds()) {
            CraftWorld craftWorld = (CraftWorld) world;
            ServerLevel nmsWorld = craftWorld.getHandle();

            for (Entity entity : nmsWorld.getEntities().getAll()) {
                if (!(entity instanceof ServerPlayer)) {
                    continue;
                }

                ServerPlayer player2 = (ServerPlayer) entity;

                if (player2.getName().toString().contains(Utils.getPrefix())) {
                    playerMaps.put(player2, craftWorld);
                }
            }
        }

        return playerMaps;
    }


    public static void info(String message) {
        Logger logger = FakePlayer.logger;
        logger.info(message);
    }

    public static String getPrefix() {
        return ChatColor.GOLD + "[" + ChatColor.GREEN + "FakePlayer" + ChatColor.GOLD + "]";
    }

    public static String getFakePlayerName(String rawName) {
        return rawName.substring(rawName.indexOf(ChatColor.AQUA + "") + 2);
    }


    public static String generateName(String name) {
        return Utils.getPrefix() + ChatColor.AQUA + name;
    }
}
