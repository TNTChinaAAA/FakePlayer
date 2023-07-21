package net.tntchina.fakeplayer;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class FakePlayerTabCompleter implements TabCompleter {

    public FakePlayer fakePlayer;

    public FakePlayerTabCompleter(FakePlayer fakePlayer) {
        this.fakePlayer = fakePlayer;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("add");
            list.add("remove");
            list.add("list");
        } else if (args.length == 2 && args[0].equals("remove")) {
            List<ServerPlayer> fakePlayers = Utils.getFakePlayersList();
            for (ServerPlayer fakePlayer : fakePlayers) {
                list.add(Utils.getFakePlayerName(fakePlayer.displayName));
            }

            list.add("all");
        }

        return list;
    }
}
