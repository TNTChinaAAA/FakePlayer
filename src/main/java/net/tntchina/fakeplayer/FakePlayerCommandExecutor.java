package net.tntchina.fakeplayer;

import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.kyori.adventure.text.Component;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedPlayerList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.scores.PlayerTeam;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.channels.spi.SelectorProvider;
import java.util.*;
import java.util.logging.Logger;

public class FakePlayerCommandExecutor implements CommandExecutor {

    public Logger logger;
    public FakePlayer fakePlayer;
    public MinecraftServer server;
    public boolean hasPlayersByNameMap = false;
    public boolean hasPlayersByUUIDMap = false;
    public Map<String, ServerPlayer> playersByName = new HashMap<>();
    public Map<UUID, ServerPlayer> playersByUUID = new HashMap<>();
    public List<ServerPlayer> players;
    public DedicatedPlayerList dedicatedPlayerList;

    public FakePlayerCommandExecutor(FakePlayer fakePlayer){
        this.fakePlayer = fakePlayer;
        this.server = MinecraftServer.getServer();
        this.logger = this.getLogger();
        this.dedicatedPlayerList = ((CraftServer) Bukkit.getServer()).getServer().getPlayerList();
        this.players = dedicatedPlayerList.players;

        for (Field field : PlayerList.class.getDeclaredFields()) {
            if (field.getType().equals(Map.class)) {
                Type t = field.getGenericType();

                if (t instanceof ParameterizedType) {
                    ParameterizedType type = (ParameterizedType) t;
                    Type[] types = type.getActualTypeArguments();

                    if (types.length == 2) {
                        boolean A = types[0].getTypeName().equals(String.class.getTypeName());
                        boolean B = types[1].getTypeName().equals(ServerPlayer.class.getTypeName());
                        boolean C = types[0].getTypeName().equals(UUID.class.getTypeName());
                        boolean D = A && B;
                        boolean E = C && B;

                        if (D) {
                            try {
                                field.setAccessible(true);
                                this.playersByName = (Map<String, ServerPlayer>) field.get(dedicatedPlayerList);
                                this.hasPlayersByNameMap = true;
                            } catch (IllegalAccessException e) {
                                this.hasPlayersByNameMap = false;
                            }
                        }

                        if (E) {
                            try {
                                field.setAccessible(true);
                                this.playersByUUID = (Map<UUID, ServerPlayer>) field.get(dedicatedPlayerList);
                                this.hasPlayersByUUIDMap = true;
                            } catch (IllegalAccessException e) {
                                this.hasPlayersByUUIDMap = false;
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args){
        this.server = MinecraftServer.getServer();
        sender.hasPermission("");
        //Bukkit.savePlayers();

        if (args.length >= 1) {
            if (args[0].equals("add")) {
                //TODO: execute the command "add".

                if (!(sender instanceof Player)) {
                    //logger.info(sender.getName()); result: CONSOLE
                    this.sendMessage("The command can only be used by player, not for the terminal operator.", ChatColor.RED, sender);

                    return true;
                }

                CraftPlayer player = (CraftPlayer) sender;

                if (args.length == 1) {
                    this.sendMessage( "The required Argument is missing: name.", ChatColor.RED, sender);
                    return false;
                } else {
                    if (args.length == 2) {
                        if (args[1].contains("[") | args[1].contains("]")) {
                            this.sendMessage("The name argument can't contain \"[\" or \"]\"! Please change a name!", ChatColor.RED, sender);
                            return true;
                        }

                        //TODO: add the specific fakePlayer from the serverSidedWorld.
                        Player player1 = Bukkit.getServer().getPlayer(args[1]);

                        if (player1 != null) {
                            boolean isFakePlayer = ((CraftPlayer) player1).getHandle() instanceof MyFakePlayer;

                            if (isFakePlayer) {
                                this.sendMessage("This fakePlayer is already in the server! Please change a name.", ChatColor.RED, sender);
                            } else {
                                this.sendMessage("This real player is already in the server! Please change a name.", ChatColor.RED, sender);
                            }

                            return true;
                        } else {
                            List<MyFakePlayer> fakePlayersList = Utils.getFakePlayersList();

                            for (MyFakePlayer fkPlayer__ : fakePlayersList) {
                                if (fkPlayer__.getNameContent().equals(args[1])) {
                                    this.sendMessage("This fakePlayer is already in the server! Please change a name.", ChatColor.RED, sender);
                                    return true;
                                }
                            }

                            CraftWorld world = (CraftWorld) player.getWorld();
                            ServerLevel nmsWorld = world.getHandle();
                            MyFakePlayer npc = new MyFakePlayer(this.server, nmsWorld, args[1]);
                            npc.setLevel(nmsWorld);
                            ServerPlayer realPlayer = player.getHandle();
                            //logger.info(realPlayer.connection.connection.channel.getClass().getName());
                            //result: NioServerSocketChannel or NettyChannelProxy(ProtocolLib Plugin Handler)
                            //Channel channel = new NioServerSocketChannel(SelectorProvider.provider(), InternetProtocolFamily.IPv4);
                            //npc.connection.connection.channel = channel;

                            /*
                            ServerGamePacketListenerImpl playerConnection = realPlayer.connection;
                            Connection connection = playerConnection.connection;
                            */
                            //logger.info(realPlayer.connection.connection.getReceiving().name()); //result: SERVERBOUND
                            //npc.connection = new ServerGamePacketListenerImpl(MinecraftServer.getServer(), new Connection(PacketFlow.SERVERBOUND), npc);
                            npc.locale = realPlayer.locale;
                            npc.setHealth(20);
                            npc.setRotation(realPlayer);
                            npc.setLocation(player);
                            npc.setYBodyRot(realPlayer.yBodyRot);
                            npc.setYHeadRot(realPlayer.yHeadRot);
                            npc.isRealPlayer = false;
                            Utils.setFakePlayerColorName(args[1], npc);//TODO: set color name.
                            Utils.addSpecificPlayer(npc, world);
                            //world.addEntityToWorld(npc, CreatureSpawnEvent.SpawnReason.CUSTOM);
                            //nmsWorld.getChunkSource().chunkMap.addDis
                            //npc.spawnIn(nmsWorld);
                            /*Chunk craftChunk = world.getChunkAt(npc.getBlockX(), npc.getBlockZ());
                            craftChunk.setForceLoaded(true);
                            craftChunk.load();
                            LevelChunk levelChunk = nmsWorld.getChunk(npc.getBlockX(), npc.getBlockZ());
                            levelChunk.setLoaded(true);
                            levelChunk.loadCallback();
                            */
                            //npc.isRealPlayer = true;
                            //nmsWorld.getChunkSource().addEntity(npc);
                            //nmsWorld.getChunkSource().chunkMap.addEntity(npc);
                            //nmsWorld.playerChunkLoader.addPlayer(npc);
                            //nmsWorld.playerChunkLoader.updatePlayer(npc);
                            //nmsWorld.playerChunkLoader.tick();
                            //npc.isRealPlayer = false;
                            //this.addPlayerToServerList(npc);
                            //nmsWorld.addNewPlayer(npc);
                            //this.updateScoreboard(nmsWorld, npc);
                            //this.dedicatedPlayerList.onPlayerJoinFinish(npc, world.getHandle(), "local");

                            ServerStatus serverping = this.server.getStatus();

                            if (serverping != null) {
                                npc.sendServerStatus(serverping);
                            }

                            npc.isRealPlayer = false;
                            //boolean A = npc.getTeam() instanceof PlayerTeam; //result: false.

                            //server.getPlayerList().placeNewPlayer(npc.connection.connection, npc);
                            //npc.sendJoinPacket(); //TODO: send joining game packet.

                            /*


                            for (String s : realPlayer.getTags()) {
                                logger.info("Tag: " + s);
                            }

                            */

                            Utils.fakePlayerList.add(npc);
                            Utils.fakePlayerMap.put(npc, world);

                            for (ServerPlayer player__ : server.getPlayerList().getPlayers()) {
                                if (player__.isRealPlayer && !(player__ instanceof MyFakePlayer)) {
                                    //server.getPlayerList().placeNewPlayer(player__.connection.connection, npc);
                                    Utils.doSending(player__.connection, npc);
                                }
                            }

                            //nmsWorld.players().add(npc);
                            this.sendMessage("Successfully added fakePlayer " + ChatColor.AQUA + args[1] + ChatColor.GREEN + ".", ChatColor.GREEN, sender);
                        }

                        return true;
                    }
                }
            }

            if (args[0].equals("remove")) {
                //TODO: execute the command "remove".

                if (args.length == 1) {
                    this.sendMessage("The required Argument is missing: name.", ChatColor.RED, sender);
                    return false;
                } else if (args.length == 2) {
                    if (args[1].contains("[") | args[1].contains("]")) {
                        this.sendMessage("The name argument can't contain \"[\" or \"]\"! Please change a name!", ChatColor.RED, sender);
                        return true;
                    }

                    //TODO: remove all fakePlayers.
                    if (args[1].equals("all")) {
                        List<UUID> lll = this.removeAllFakePlayer();

                        for (ServerPlayer player__ : server.getPlayerList().getPlayers()) {
                            if (player__.isRealPlayer && !(player__ instanceof MyFakePlayer)) {
                                Utils.doRemoving(player__.connection, lll);
                            }
                        }

                        this.sendMessage("Successfully remove all fakePlayers.", ChatColor.GREEN, sender);
                        return true;
                    }

                    //TODO: delete the specific fakePlayer from the serverSidedWorld.
                    Map<MyFakePlayer, CraftWorld> fkPlayerMaps = Utils.getFakePlayerMaps();
                    List<MyFakePlayer> fkPlayersList = new ArrayList<>(fkPlayerMaps.keySet());
                    boolean isFakeExisting = false;
                    UUID uuid__ = UUID.randomUUID();

                    for (MyFakePlayer pla_ : fkPlayersList) {
                        if (pla_.getNameContent().equals(args[1])) {
                            ServerLevel nmsWorld = fkPlayerMaps.get(pla_).getHandle();
                            Utils.removeSpecificPlayerFromList(pla_, nmsWorld); //TODO: remove the player from some lists.
                            pla_.setHealth(0);
                            pla_.kill();
                            nmsWorld.removePlayerImmediately(pla_, Entity.RemovalReason.KILLED);
                            isFakeExisting = true;
                            uuid__ = pla_.getUUID();
                            this.removePlayerFromServerList(pla_);


                            for (ServerPlayer player__ : server.getPlayerList().getPlayers()) {
                                if (player__.isRealPlayer && !(player__ instanceof MyFakePlayer)) {
                                    Utils.doRemoving(player__.connection, pla_);
                                }
                            }

                            break;
                        }
                    }


                    if (isFakeExisting) {
                        Player player1 = Bukkit.getServer().getPlayer(uuid__);

                        if (player1 != null) {

                        /*
                            if (player1 instanceof ServerPlayer) {

                                ServerPlayer player4 = ((ServerPlayer) player1);
                                player4.setHealth(0);
                                player4.kill();
                                CraftWorld wddd =  ((CraftWorld) player1.getWorld());
                                wddd.getHandle().removePlayerImmediately(player4, Entity.RemovalReason.KILLED);
                                wddd.getHandle().getChunkSource().removeEntity(player4);
                            }
                        */

                            CraftPlayer player = (CraftPlayer) player1;
                            CraftWorld worlddd = (CraftWorld) player.getWorld();
                            ServerLevel level__ = worlddd.getHandle();

                            if (player.getHandle() != null) {
                                if (player.getHandle() instanceof MyFakePlayer) {
                                    MyFakePlayer fkkk = (MyFakePlayer) player.getHandle();
                                    Utils.removeSpecificPlayerFromList(fkkk, level__); //TODO: remove the player from some lists.
                                    player1.setHealth(0);
                                    fkkk.kill();
                                    player1.kick();
                                    level__.removePlayerImmediately(fkkk, Entity.RemovalReason.KILLED);
                                }
                            }
                        }
                    }


                    if (!isFakeExisting) {
                        this.sendMessage("Error! The fakePlayer called " + args[1] + " is not existing!", ChatColor.RED, sender);
                    } else {
                        /*if (player1 != null) {
                            if (player1.getPlayerListName().contains(Utils.getPrefix())) {
                                if (player1 instanceof ServerPlayer) {
                                    ServerPlayer player4 = ((ServerPlayer) player1);
                                    player4.setHealth(0);
                                    player4.kill();
                                    ((CraftWorld) player1.getWorld()).getHandle().removePlayerImmediately(player4, Entity.RemovalReason.KILLED);
                                }

                                player1.setHealth(0);
                                player1.kick();
                            } else {
                                this.broadcastMessage("This is a real player! Please enter the fakePlayer name!", ChatColor.RED);
                                return true;
                            }
                        }*/

                        this.sendMessage("Successfully remove fakePlayer " + ChatColor.AQUA + args[1] + ChatColor.GREEN + ".", ChatColor.GREEN, sender);
                    }

                    return true;
                }
            }

            if (args[0].equals("list")) {
                //TODO: execute the command "list".

                if (args.length == 1) {
                    //TODO: List the fakePlayer(s).
                    this.listAllPlayer(Utils.getFakePlayerMaps(), sender);
                    return true;
                }
            }
        }

        this.sendWrongMessage(sender);
        return false;
    }

    @Deprecated
    public void updateScoreboard(ServerLevel nmsWorld, MyFakePlayer npc) {
        this.dedicatedPlayerList.updateEntireScoreboard(nmsWorld.getScoreboard(), npc);
    }

    @Deprecated
    public void addPlayerToServerList(MyFakePlayer fakePlayer) {
        GameProfile gameprofile = fakePlayer.getGameProfile();
        GameProfileCache usercache = this.server.getProfileCache();
        //String s;
        if (usercache != null) {
            //Optional<GameProfile> optional = usercache.get(gameprofile.getId());
            //s = (String)optional.map(GameProfile::getName).orElse(gameprofile.getName());
            usercache.add(gameprofile);
        }

        /*
        else {

            //s = gameprofile.getName();
        }*/

        /*
        if (this.hasPlayersByNameMap) {
            this.playersByName.put(fakePlayer.getNameContent(), fakePlayer);
        }*/

        /*
        if (this.hasPlayersByUUIDMap) {
            this.playersByUUID.put(fakePlayer.getUUID(), fakePlayer);
        }*/

        this.players.add(fakePlayer);
    }

    public void removePlayerFromServerList(MyFakePlayer fakePlayer) {
        /*
        if (this.hasPlayersByNameMap) {
            this.playersByName.remove(fakePlayer.getNameContent(), fakePlayer);
        }*/

        /*
        if (this.hasPlayersByUUIDMap) {
            this.playersByUUID.remove(fakePlayer.getUUID(), fakePlayer);
        }*/

        this.players.remove(fakePlayer);
    }

    @Deprecated
    public void listNames() {
        if (this.hasPlayersByUUIDMap) {
            for (Map.Entry<UUID, ServerPlayer> entry : playersByUUID.entrySet()) {
                Utils.info("Player:{listName: " +entry.getValue().listName + "}, {name: " + entry.getValue().getName().toString() + "}, {UUID: " + entry.getValue().getStringUUID() + "}");
            }
        }

        if (this.hasPlayersByNameMap) {
            for (Map.Entry<String, ServerPlayer> entry : playersByName.entrySet()) {
                Utils.info("Player:{listName: " +entry.getValue().listName + "}, {name: " + entry.getKey() + "}, {UUID: " + entry.getValue().getStringUUID() + "}");
            }
        }
    }

    public void listAllPlayer(Map<MyFakePlayer, CraftWorld> playerMaps, CommandSender sender) {
        List<MyFakePlayer> list = new ArrayList<>(playerMaps.keySet());

        if (list.size() == 0) {
            this.sendMessage("There are no fakePlayers.", ChatColor.GREEN, sender);
        }

        if (list.size() == 1) {
            MyFakePlayer fakePlayer_1 = list.get(0);
            String name = fakePlayer_1.getNameContent();
            int x = (int) fakePlayer_1.getX();
            int y = (int) fakePlayer_1.getY();
            int z = (int) fakePlayer_1.getZ();
            String position = "[X=" + x + ", " + "Y=" + y + ", " + "Z=" + z + "]";
            CraftWorld cw = playerMaps.get(fakePlayer_1);
            String wd_str = "[World: " + cw.getName() + "]";
            this.sendMessage(ChatColor.GREEN + "There is a fakePlayer: " + ChatColor.AQUA + name + ChatColor.GREEN + position + wd_str + ".", sender);
        }

        if (list.size() >= 2) {
            String ms_12 = "There are " + list.size() + " fakePlayers, they are: ";

            for (int i = 0; i < list.size(); i++) {
                MyFakePlayer player__1 = list.get(i);
                String name_4 = player__1.getNameContent();
                ms_12 += ChatColor.AQUA + name_4 + ChatColor.GREEN;
                int x = (int) player__1.getX();
                int y = (int) player__1.getY();
                int z = (int) player__1.getZ();
                String position = "[X=" + x + ", " + "Y=" + y + ", " + "Z=" + z + "]";
                ms_12 += position;
                CraftWorld cw_1 = playerMaps.get(player__1);
                String wd_str1 = "[World: " + cw_1.getName() + "]";
                ms_12 += wd_str1;
                ms_12 += (i == list.size() - 1) ? "." : ", ";
            }

            this.sendMessage(ms_12, ChatColor.GREEN, sender);
        }
    }

    public List<UUID> removeAllFakePlayer() {
        List<UUID> removeList = new ArrayList<>();

        for(Map.Entry<MyFakePlayer, CraftWorld> entry : Utils.getFakePlayerMaps().entrySet()) {
            MyFakePlayer npc = entry.getKey();
            ServerLevel nmsWorld = entry.getValue().getHandle();

            if (nmsWorld.players().contains(npc)) {
                nmsWorld.players().remove(npc);
            }

            removeList.add(npc.getUUID());
            npc.setHealth(0);
            npc.kill();

            Player player1 = Bukkit.getServer().getPlayer(npc.getUUID()); //important

            if (player1 != null) {
                CraftPlayer player = (CraftPlayer) player1;

                if (player.getHandle() != null) {
                    if (player.getHandle() instanceof MyFakePlayer) {
                        player1.setHealth(0);
                        player1.kick();
                    }
                }
            }

            nmsWorld.removePlayerImmediately(npc, Entity.RemovalReason.KILLED);
            Utils.removeSpecificPlayer(npc, nmsWorld);
            this.removePlayerFromServerList(npc);
        }

        Utils.fakePlayerList = new ArrayList<>();
        Utils.fakePlayerMap = new HashMap<>();
        return removeList;
    }

    /*
    public void broadcastWrongMessage() {
        Bukkit.broadcastMessage(ChatColor.AQUA + "[FakePlayer] " + ChatColor.RED + "Wrong usage! Please according to the usage guide.");
    }

    public void broadcastMessage(String message) {
        Bukkit.broadcastMessage(ChatColor.AQUA + "[FakePlayer] " + message);
    }

    public void broadcastMessage(String message, ChatColor chatColor) {
        Bukkit.broadcastMessage(ChatColor.AQUA + "[FakePlayer] " + chatColor + message);
    }*/

    public void sendMessage(String message, CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "[FakePlayer] " + message);
    }

    public void sendMessage(String message, ChatColor chatColor, CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "[FakePlayer] " + chatColor + message);
    }

    public void sendWrongMessage(CommandSender sender) {
        this.sendMessage("Wrong usage! Please according to the usage guide.", ChatColor.RED, sender);
    }

    public Logger getLogger() {
        return this.fakePlayer.getLogger();
    }
}
