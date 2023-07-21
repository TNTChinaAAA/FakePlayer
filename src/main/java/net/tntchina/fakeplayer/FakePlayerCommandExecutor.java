package net.tntchina.fakeplayer;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.*;
import java.util.logging.Logger;

public class FakePlayerCommandExecutor implements CommandExecutor {

    FakePlayer fakePlayer;

    public FakePlayerCommandExecutor(FakePlayer fakePlayer){
        this.fakePlayer = fakePlayer;
    }
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        Logger logger = this.getLogger();
        MinecraftServer server = MinecraftServer.getServer();

        if (args.length >= 1) {
            if (args[0].equals("add")) {
                //TODO: execute the command "add".

                if (!(sender instanceof Player)) {
                    logger.info(ChatColor.RED + "The command can only be used by player, not for the terminal operator.");
                    return true;
                }

                Player player = (Player) sender;

                if (args.length == 1) {
                    this.broadcastMessage("The required Argument is missing: name.", ChatColor.RED);
                    return false;
                } else {
                    if (args.length == 2) {
                        if (args[1].contains("[") | args[1].contains("]")) {
                            this.broadcastMessage("The name argument can't contain \"[\" or \"]\"! Please change a name!", ChatColor.RED);
                            return true;
                        }

                        //TODO: add the specific fakePlayer from the serverSidedWorld.
                        Player player1 = Bukkit.getServer().getPlayer(args[1]);

                        if (player1 != null) {
                            boolean isFakePlayer = player1.getName().contains(Utils.getPrefix());

                            if (isFakePlayer) {
                                this.broadcastMessage("This fakePlayer is already in the server!Please change a name.", ChatColor.RED);
                            } else {
                                this.broadcastMessage("This real player is already in the server!Please change a name.", ChatColor.RED);
                            }

                            return true;
                        } else {
                            List<ServerPlayer> fakePlayersList = Utils.getFakePlayersList();

                            for (ServerPlayer fkPlayer__ : fakePlayersList) {
                                if (Utils.getFakePlayerName(fkPlayer__.displayName).equals(args[1])) {
                                    this.broadcastMessage("This fakePlayer is already in the server! Please change a name.", ChatColor.RED);
                                    return true;
                                }
                            }

                            CraftWorld world = (CraftWorld) player.getWorld();
                            ServerLevel nmsWorld = world.getHandle();
                            GameProfile profile = new GameProfile(UUID.randomUUID(), Utils.generateName(args[1]));
                            ServerPlayer npc = new MyFakePlayer(server, nmsWorld, profile);
                            ServerPlayer realPlayer = Utils.getServerPlayerByName(player.getName(), nmsWorld);
                            /*
                            ServerGamePacketListenerImpl playerConnection = realPlayer.connection;
                            Connection connection = playerConnection.connection;
                            */
                            npc.connection = new ServerGamePacketListenerImpl(MinecraftServer.getServer(), new Connection(PacketFlow.SERVERBOUND), npc);
                            npc.displayName = Utils.generateName(args[1]);
                            npc.setXRot(realPlayer.getXRot());
                            npc.setYRot(realPlayer.getYRot()); //TODO: 让npc视角和玩家一模一样
                            Location location = player.getLocation();
                            npc.setPos(location.getX(), location.getY(), location.getZ());
                            npc.visibleByDefault = true;
                            npc.setGameMode(GameType.CREATIVE);
                            //nmsWorld.addNewPlayer(npc);
                            world.addEntityToWorld(npc, CreatureSpawnEvent.SpawnReason.CUSTOM);
                            Player npc_1 = Bukkit.getPlayer(args[1]);

                            if (npc_1 != null) {
                                npc_1.setDisplayName(Utils.generateName(args[1]));
                                npc_1.setPlayerListName(Utils.generateName(args[1]));
                            }

                            this.broadcastMessage("Successfully added fakePlayer " + ChatColor.AQUA + args[1] + ChatColor.GREEN + ".", ChatColor.GREEN);
                        }

                        return true;
                    } else {
                        this.broadcastWrongMessage();
                        return false;
                    }
                }
            }

            if (args[0].equals("remove")) {
                //TODO: execute the command "remove".

                if (args.length == 1) {
                    this.broadcastMessage("The required Argument is missing: name.", ChatColor.RED);
                    return false;
                } else if (args.length == 2) {
                    if (args[1].contains("[") | args[1].contains("]")) {
                        this.broadcastMessage("The name argument can't contain \"[\" or \"]\"! Please change a name!", ChatColor.RED);
                        return true;
                    }

                    Map<ServerPlayer, CraftWorld> playerMaps = Utils.getFakePlayerMaps();
                    //TODO: remove all fakePlayers.
                    if (args[1].equals("all")) {
                        this.removeAllFakePlayer();
                        this.broadcastMessage("Successfully remove all fakePlayers.", ChatColor.GREEN);
                        return true;
                    }

                    //TODO: delete the specific fakePlayer from the serverSidedWorld.
                    Map<ServerPlayer, CraftWorld> fkPlayerMaps = Utils.getFakePlayerMaps();
                    List<ServerPlayer> fkPlayersList = new ArrayList<>(fkPlayerMaps.keySet());
                    boolean isFakeExisting = false;

                    for (ServerPlayer pla_ : fkPlayersList) {
                        if (Utils.getFakePlayerName(pla_.displayName).equals(args[1])) {
                            pla_.setHealth(0);
                            pla_.kill();
                            ServerLevel nmsWorld = fkPlayerMaps.get(pla_).getHandle();
                            nmsWorld.removePlayerImmediately(pla_, Entity.RemovalReason.KILLED);
                            isFakeExisting = true;
                            break;
                        }
                    }

                    Player player1 = Bukkit.getServer().getPlayer(Utils.generateName(args[1]));

                    if (player1 != null) {
                        if (player1.getPlayerListName().contains(Utils.getPrefix())) {
                            if (player1 instanceof ServerPlayer) {
                                ServerPlayer player4 = ((ServerPlayer) player1);
                                player4.setHealth(0);
                                player4.kill();
                                ((CraftWorld) player1.getWorld()).getHandle().removePlayerImmediately(player4, Entity.RemovalReason.KILLED);
                            }

                            player1.setHealth(0);
                            player1.kick();
                        }
                    }

                    if (!isFakeExisting) {
                        this.broadcastMessage("Error! The fakePlayer called " + args[1] + " is not existing!", ChatColor.RED);
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

                        this.broadcastMessage("Successfully remove fakePlayer " + ChatColor.AQUA + args[1] + ChatColor.GREEN + ".", ChatColor.GREEN);
                    }

                    return true;
                } else {
                    this.broadcastWrongMessage();
                    return false;
                }
            }

            if (args[0].equals("list")) {
                //TODO: execute the command "list".

                if (args.length == 1) {
                    //TODO: List the fakePlayer(s).
                    this.listAllPlayer(Utils.getFakePlayerMaps());
                    return true;
                } else {
                    this.broadcastWrongMessage();
                    return false;
                }
            }
        } else {
            this.broadcastWrongMessage();
            return false;
        }

        return false;
    }

    public void listAllPlayer(Map<ServerPlayer, CraftWorld> playerMaps) {
        List<ServerPlayer> list = new ArrayList<>(playerMaps.keySet());

        if (list.size() == 0) {
            this.broadcastMessage("There are no fakePlayers.", ChatColor.GREEN);
        }

        if (list.size() == 1) {
            ServerPlayer fakePlayer_1 = list.get(0);
            String name = Utils.getFakePlayerName(fakePlayer_1.displayName);
            int x = (int) fakePlayer_1.getX();
            int y = (int) fakePlayer_1.getY();
            int z = (int) fakePlayer_1.getZ();
            String position = "[X=" + x + ", " + "Y=" + y + ", " + "Z=" + z + "]";
            CraftWorld cw = playerMaps.get(fakePlayer_1);
            String wd_str = "[World: " + cw.getName() + "]";
            this.broadcastMessage(ChatColor.GREEN + "There is a fakePlayer: " + ChatColor.AQUA + name + ChatColor.GREEN + position + wd_str + ".");
        }

        if (list.size() >= 2) {
            String ms_12 = "There are " + list.size() + " fakePlayers, they are: ";

            for (int i = 0; i < list.size(); i++) {
                ServerPlayer player__1 = list.get(i);
                String name_4 = Utils.getFakePlayerName(player__1.displayName);
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

            this.broadcastMessage(ms_12, ChatColor.GREEN);
        }
    }

    public void removeAllFakePlayer() {
        for(Map.Entry<ServerPlayer, CraftWorld> entry : Utils.getFakePlayerMaps().entrySet()) {
            ServerPlayer npc = entry.getKey();
            ServerLevel nmsWorld = entry.getValue().getHandle();
            npc.setHealth(0);
            npc.kill();
            nmsWorld.removePlayerImmediately(npc, Entity.RemovalReason.KILLED);
        }
    }

    public void broadcastWrongMessage() {
        Bukkit.broadcastMessage(ChatColor.AQUA + "[FakePlayer] " + ChatColor.RED + "Wrong usage! Please according to the usage guide.");
    }

    public void broadcastMessage(String message) {
        Bukkit.broadcastMessage(ChatColor.AQUA + "[FakePlayer] " + message);
    }

    public void broadcastMessage(String message, ChatColor chatColor) {
        Bukkit.broadcastMessage(ChatColor.AQUA + "[FakePlayer] " + chatColor + message);
    }

    public Logger getLogger() {
        return this.fakePlayer.getLogger();
    }
}
