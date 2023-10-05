package net.tntchina.fakeplayer;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.File;
import java.util.logging.Logger;

public final class FakePlayer extends JavaPlugin implements Listener {

    public static File folder;
    public static  ConfigManager configManager;
    public static FakePlayer fakePlayer;
    public static Logger logger;
    public static FakePlayerTabCompleter tabCompleter;
    public static PlayerListener playerListener;
    //public static SystemGCListener SystemGCListener;
    public static FakePlayerCommandExecutor commandExecutor;

    @Override
    public void onEnable() {
        // Plugin startup logic
        FakePlayer.fakePlayer = this;
        FakePlayer.logger = this.getLogger();
        FakePlayer.folder = Bukkit.getServer().getPluginManager().getPlugin("FakePlayer").getDataFolder();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        FakePlayer.configManager = new ConfigManager(this);
        FakePlayer.playerListener = new PlayerListener(this);
        //FakePlayer.SystemGCListener = new SystemGCListener(this);
        FakePlayer.tabCompleter = new FakePlayerTabCompleter(this);
        FakePlayer.commandExecutor = new FakePlayerCommandExecutor(this);
        FakePlayer.configManager.load();
        getServer().getPluginManager().registerEvents(FakePlayer.playerListener, this); //TODO: register player listener.
        //getServer().getPluginManager().registerEvents(FakePlayer.SystemGCListener, this); //TODO: register tick listener
        getCommand("fakePlayer").setExecutor(FakePlayer.commandExecutor); //TODO: set command executor.
        getCommand("fakePlayer").setTabCompleter(FakePlayer.tabCompleter);//TODO: set command completer.
        BukkitScheduler scheduler = Bukkit.getScheduler();
        //scheduler.scheduleSyncRepeatingTask(this, new SystemGCRunnable(), 300, 300);
        this.getLogger().info("Enabled FakePlayer Plugin");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        FakePlayer.configManager.save();
        this.getLogger().info("Saving...");
        FakePlayer.commandExecutor.removeAllFakePlayer();
    }

    public static File getFolder() {
        return FakePlayer.folder;
    }

    /*
    public static PluginCommand getCmd() {
        return FakePlayer.fakePlayer.getCommand("fakePlayer");
    }
     */
}
