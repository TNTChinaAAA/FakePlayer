package net.tntchina.fakeplayer;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    public FakePlayer fakePlayer;
    public PlayerListener(FakePlayer fakePlayer) {
        this.fakePlayer = fakePlayer;
    }


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        CraftPlayer player = (CraftPlayer) event.getPlayer();

        if (player.getHandle().isRealPlayer && !(player.getHandle() instanceof MyFakePlayer)) {
            for (MyFakePlayer fkPlayer : Utils.getFakePlayersList()) {
                //MinecraftServer.getServer().getPlayerList().placeNewPlayer(player.getHandle().connection.connection, fkPlayer);
                Utils.doSending(player.getHandle().connection, fkPlayer);
            }
        }

        //FakePlayer.commandExecutor.listNames();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        /*
        CraftPlayer player = (CraftPlayer) event.getPlayer();
        ServerPlayer p = player.getHandle();

        if (p instanceof MyFakePlayer) {

        }
         */
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        CraftPlayer player = (CraftPlayer) event.getPlayer();

        if (player.getHandle().isRealPlayer && !(player.getHandle() instanceof MyFakePlayer)) {

            for (MyFakePlayer fkPlayer : Utils.getFakePlayersList()) {
                //Utils.doSending(player.getHandle().connection, fkPlayer);
                //Utils.doRemoving(player.getHandle().connection, fkPlayer);
                Utils.doSending(player.getHandle().connection, fkPlayer);
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        CraftPlayer player = (CraftPlayer) event.getPlayer();

        if (player.getHandle().isRealPlayer && !(player.getHandle() instanceof MyFakePlayer)) {
            for (MyFakePlayer fkPlayer : Utils.getFakePlayersList()) {
                Utils.doSending(player.getHandle().connection, fkPlayer);
            }
        }
    }
}
