package net.tntchina.fakeplayer;

import net.kyori.adventure.text.Component;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Scoreboard;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.scoreboard.CraftScoreboard;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;

public class Utils {

    public static Field lastOverloadWarningField = null;
    public static Method addPlayerToDistanceMapsMethod = null;
    public static Method removePlayerFromDistanceMapsMethod = null;

    static {
        try {
            Utils.lastOverloadWarningField = MinecraftServer.class.getDeclaredField("ae");
            Utils.addPlayerToDistanceMapsMethod = ChunkMap.class.getDeclaredMethod("addPlayerToDistanceMaps", ServerPlayer.class);
            Utils.removePlayerFromDistanceMapsMethod = ChunkMap.class.getDeclaredMethod("removePlayerFromDistanceMaps", ServerPlayer.class);
            Utils.lastOverloadWarningField.setAccessible(true);
            Utils.addPlayerToDistanceMapsMethod.setAccessible(true);
            Utils.removePlayerFromDistanceMapsMethod.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<MyFakePlayer> fakePlayerList = new ArrayList<>();
    public static Map<MyFakePlayer, CraftWorld> fakePlayerMap = new HashMap<>();

    @Deprecated
    public static ServerPlayer getServerPlayerByName(String name, ServerLevel world) {
        for (ServerPlayer player : world.players()) {
            net.minecraft.network.chat.Component component = player.getName();
            String n = component.toString();

            if (n.equals("literal{" + name + "}")) {
                return player;
            }
        }

        return null;
    }

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
    public static List<MyFakePlayer> o_getFakePlayersList() {
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

    public static List<MyFakePlayer> getFakePlayersList() {
        return Utils.fakePlayerList;
    }

    public static Map<MyFakePlayer, CraftWorld> getFakePlayerMaps() {
        return Utils.fakePlayerMap;
    }

    @Deprecated
    public static Map<MyFakePlayer, CraftWorld> o_getFakePlayerMaps() {
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

    @Deprecated
    public static String getFakePlayerName(String rawName) {
        return rawName.substring(rawName.indexOf(ChatColor.AQUA + "") + 2);
    }


    public static String generateName(String name) {
        return Utils.getPrefix() + ChatColor.AQUA + name;
    }

    @Deprecated
    public static void doAddingEntity(ServerGamePacketListenerImpl connection, MyFakePlayer fkPlayer) {
        connection.send(new ClientboundAddEntityPacket(fkPlayer));
    }

    public static void doUpdatePlayer(ServerGamePacketListenerImpl connection, MyFakePlayer fkPlayer) {
        connection.send(new ClientboundPlayerInfoUpdatePacket(Action.UPDATE_LISTED, fkPlayer));
        connection.send(new ClientboundPlayerInfoUpdatePacket(Action.UPDATE_DISPLAY_NAME, fkPlayer));
    }

    public static void doSending(ServerGamePacketListenerImpl connection, MyFakePlayer fkPlayer) {
        connection.send(new ClientboundPlayerInfoUpdatePacket(Action.ADD_PLAYER, fkPlayer)); //TODO: send update player info packet.
        connection.send(new ClientboundAddPlayerPacket(fkPlayer));
        Utils.doUpdatePlayer(connection, fkPlayer);
    }

    public static void doRemoving(ServerGamePacketListenerImpl connection, MyFakePlayer fkPlayer) {
        List<UUID> l = new ArrayList<>();
        l.add(fkPlayer.getUUID());
        connection.send(new ClientboundPlayerInfoRemovePacket(l));
        connection.send(new ClientboundRemoveEntitiesPacket(fkPlayer.getId()));
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

    public static void addSpecificPlayer(MyFakePlayer fakePlayer, CraftWorld world) {
        ServerLevel nmsWorld = world.getHandle();
        fakePlayer.isRealPlayer = true;
        world.addEntityToWorld(fakePlayer, CreatureSpawnEvent.SpawnReason.CUSTOM);

        /*
        if (!nmsWorld.playersAffectingSpawning.contains(fakePlayer)) {
            nmsWorld.playersAffectingSpawning.add(fakePlayer);
        }

        if (!nmsWorld.players().contains(fakePlayer)) {
            nmsWorld.players().add(fakePlayer);
        }
        */

        //Utils.addPlayerToDistanceMaps(nmsWorld.getChunkSource().chunkMap, fakePlayer);
        fakePlayer.isRealPlayer = false;
    }

    public static void removeSpecificPlayerFromList(MyFakePlayer fakePlayer, ServerLevel nmsWorld) {
        Utils.fakePlayerList.remove(fakePlayer);
        Utils.fakePlayerMap.remove(fakePlayer);
        Utils.removeSpecificPlayer(fakePlayer, nmsWorld);
    }

    public static void removeSpecificPlayer(MyFakePlayer fakePlayer, ServerLevel nmsWorld) {
        fakePlayer.isRealPlayer = true;
        nmsWorld.players().remove(fakePlayer);
        //Utils.removePlayerFromDistanceMaps(nmsWorld.getChunkSource().chunkMap, fakePlayer);
        nmsWorld.getChunkSource().removeEntity(fakePlayer);
        nmsWorld.playerChunkLoader.removePlayer(fakePlayer);
        nmsWorld.playersAffectingSpawning.remove(fakePlayer);
        fakePlayer.isRealPlayer = false;
    }

    @Deprecated
    public static void addPlayerToDistanceMaps(ChunkMap chunkMap, ServerPlayer player) {
        try {
            if (Utils.addPlayerToDistanceMapsMethod != null) {
                Utils.addPlayerToDistanceMapsMethod.invoke(chunkMap, player);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public static void removePlayerFromDistanceMaps(ChunkMap chunkMap, ServerPlayer player) {
        try {
            if (Utils.removePlayerFromDistanceMapsMethod != null) {
                Utils.removePlayerFromDistanceMapsMethod.invoke(chunkMap, player);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Deprecated
    public static CraftScoreboard getCraftScoreboard(Scoreboard scoreboard) {
        //CraftScoreboardManager scoreboardManager = (CraftScoreboardManager) Bukkit.getScoreboardManager();
        Constructor<CraftScoreboard> cons = null;

        try {
            cons = CraftScoreboard.class.getDeclaredConstructor(Scoreboard.class);

            if (cons != null) {
                cons.setAccessible(true);
                return cons.newInstance(scoreboard);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Deprecated
    public static boolean isOverloaded() { //This method didn't work
        MinecraftServer server = MinecraftServer.getServer();
        long nextTickTime = server.getNextTickTime();
        long lastOverloadWarning = 0;
        long i = (System.nanoTime()) / 1000000L - nextTickTime;

        if (Utils.lastOverloadWarningField != null) {
            try {
                lastOverloadWarning = (long) Utils.lastOverloadWarningField.get(server);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return false;
        }

        if (i > 5000L && server.getNextTickTime() - lastOverloadWarning >= 30000L) {
            return true;
        }

        return false;
    }
}
