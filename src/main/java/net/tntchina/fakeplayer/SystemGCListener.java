package net.tntchina.fakeplayer;

import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.*;

@Deprecated
public class SystemGCListener implements Listener {

    public FakePlayer fakePlayer;

    public SystemGCListener(FakePlayer fakePlayer) {
        this.fakePlayer = fakePlayer;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        System.gc();
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        System.gc();
    }

    @EventHandler
    public void onChunkPopulate(ChunkPopulateEvent event) {
        System.gc();
    }

    @EventHandler
    public void onSpawnChange(SpawnChangeEvent event) {
        System.gc();
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        System.gc();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        System.gc();
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        System.gc();
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        System.gc();
    }

    @EventHandler
    public void onItemDespawn(ItemDespawnEvent event) {
        System.gc();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        System.gc();
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        System.gc();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        System.gc();
    }
}
