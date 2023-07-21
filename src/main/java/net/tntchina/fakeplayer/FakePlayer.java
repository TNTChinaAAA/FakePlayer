package net.tntchina.fakeplayer;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public final class FakePlayer extends JavaPlugin implements Listener {

    public static FakePlayer fakePlayer;
    public static Logger logger;
    public static FakePlayerTabCompleter fakePlayerTabCompleter;
    public static FakePlayerCommandExecutor fakePlayerCommandExecutor;

    @Override
    public void onEnable() {
        // Plugin startup logic
        FakePlayer.fakePlayer = this;
        FakePlayer.logger = this.getLogger();
        FakePlayer.fakePlayerTabCompleter = new FakePlayerTabCompleter(this);
        FakePlayer.fakePlayerCommandExecutor = new FakePlayerCommandExecutor(this);
        getCommand("fakePlayer").setExecutor(FakePlayer.fakePlayerCommandExecutor); //TODO: set command executor.
        getCommand("fakePlayer").setTabCompleter(FakePlayer.fakePlayerTabCompleter);//TODO: set command completer.
        this.getLogger().info("Enabled FakePlayer Plugin");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }




}
