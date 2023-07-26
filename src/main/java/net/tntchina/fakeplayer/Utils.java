package net.tntchina.fakeplayer;

import net.kyori.adventure.text.Component;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.*;
import java.util.logging.Logger;

public class Utils {

    /*
    public static ServerPlayer getServerPlayerByName(String name, ServerLevel world) {
        for (ServerPlayer player : world.players()) {
            Component component = player.getName();
            String n = component.toString();

            if (n.equals("literal{" + name + "}")) {
                return player;
            }
        }

        return null;
    }*/

    /*
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
    }*/

    public static List<MyFakePlayer> getFakePlayersList() {
        List<MyFakePlayer> list = new ArrayList<>();

        for(World world : Bukkit.getWorlds()) {
            CraftWorld craftWorld = (CraftWorld) world;
            ServerLevel nmsWorld = craftWorld.getHandle();

            for (Entity entity : nmsWorld.getEntities().getAll()) {
                if (!(entity instanceof ServerPlayer)) {
                    continue;
                }

                ServerPlayer player2 = (ServerPlayer) entity;

                if (player2 instanceof MyFakePlayer) {
                    list.add((MyFakePlayer) player2);
                }
            }
        }

        return list;
    }

    public static Map<MyFakePlayer, CraftWorld> getFakePlayerMaps() {
        Map<MyFakePlayer, CraftWorld> playerMaps = new HashMap<>();

        for(World world : Bukkit.getWorlds()) {
            CraftWorld craftWorld = (CraftWorld) world;
            ServerLevel nmsWorld = craftWorld.getHandle();

            for (Entity entity : nmsWorld.getEntities().getAll()) {
                if (!(entity instanceof ServerPlayer)) {
                    continue;
                }

                ServerPlayer player2 = (ServerPlayer) entity;

                if (player2 instanceof MyFakePlayer) {
                    playerMaps.put((MyFakePlayer) player2, craftWorld);
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

    /*
    public static String getFakePlayerName(String rawName) {
        return rawName.substring(rawName.indexOf(ChatColor.AQUA + "") + 2);
    }*/


    public static String generateName(String name) {
        return Utils.getPrefix() + ChatColor.AQUA + name;
    }

    public static void doSending(ServerGamePacketListenerImpl connection, MyFakePlayer fkPlayer) {
        connection.send(new ClientboundPlayerInfoUpdatePacket(Action.ADD_PLAYER, fkPlayer)); //TODO: send update player info packet.
        connection.send(new ClientboundAddPlayerPacket(fkPlayer));
        connection.send(new ClientboundPlayerInfoUpdatePacket(Action.UPDATE_LISTED, fkPlayer));
        connection.send(new ClientboundPlayerInfoUpdatePacket(Action.UPDATE_DISPLAY_NAME, fkPlayer));
    }

    public static void doRemoving(ServerGamePacketListenerImpl connection, MyFakePlayer fkPlayer) {
        List<UUID> l = new ArrayList<>();
        l.add(fkPlayer.getUUID());
        connection.send(new ClientboundPlayerInfoRemovePacket(l));
        //TODO: send remove player info packet.
    }


    public static void doRemoving(ServerGamePacketListenerImpl connection, List<UUID> list) {
        connection.send(new ClientboundPlayerInfoRemovePacket(list));
    }


    public static Map<String, CraftWorld> getWorldMaps() {
        Map<String, CraftWorld> maps = new HashMap<>();

        for (World wd : Bukkit.getWorlds()) {
            CraftWorld world_ = (CraftWorld) wd;

            if (world_ != null) {
                maps.put(world_.getName(), world_);
            }
        }

        return maps;
    }

    public static void setFakePlayerColorName(String name, MyFakePlayer npc) {
        String btName = Utils.generateName(name);
        npc.adventure$displayName = Component.text(btName); //TODO: update adventure name
        npc.displayName = btName;
        npc.listName = CraftChatMessage.fromStringOrNull(btName);
        npc.setCustomNameVisible(true);
        npc.setCustomName(CraftChatMessage.fromStringOrNull(btName));
        //TODO: set color name
    }
}
