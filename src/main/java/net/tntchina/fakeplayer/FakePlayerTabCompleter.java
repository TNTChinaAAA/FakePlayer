package net.tntchina.fakeplayer;

import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

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
            if (args[0].equals("") | args[0].equals(" ") | args[0].replace(" ", "").equals("")) {
                list.add("add");
                list.add("remove");
                list.add("list");
                list.add("chunkStatus");
                return list;
            }

            if (this.getCharsListContain("chunkStatus", args[0])) {
                list.add("chunkStatus");
                return list;
            }

            if (this.getCharsListContain("add", args[0])) {
                list.add("add");
                return list;
            }

            if (this.getCharsListContain("remove", args[0])) {
                list.add("remove");
                return list;
            }

            if (this.getCharsListContain("list", args[0])) {
                list.add("list");
                return list;
            }
        } else if (args.length == 2 && args[0].equals("remove")) {
            if (args[1].equals("") | args[1].equals(" ") | args[1].replace(" ", "").equals("")) {
                List<MyFakePlayer> fakePlayerss = Utils.getFakePlayersList();

                for (MyFakePlayer fakePlayer : fakePlayerss) {
                    list.add(fakePlayer.getNameContent());
                }

                list.add("all");
                return list;
            } else if (this.getCharsListContain("all", args[1])) {
                list.add("all");
                return list;
            } else {
                List<MyFakePlayer> fakePlayers = Utils.getFakePlayersList();

                for (MyFakePlayer fakePlayer : fakePlayers) {
                    if (this.getCharsListContain(fakePlayer.getNameContent(), args[1])) {
                        list.add(fakePlayer.getNameContent());
                    }
                }

                return list;
            }
        } else if (args.length == 2 && args[0].equals("chunkStatus")) {
            if (!(commandSender instanceof Player)) {
                //TODO: send world tab complete

                for (World world : Bukkit.getWorlds()) {
                    list.add(world.getName());
                }
            }
        }

        return list;
    }

    /*
    public List<String> getCharsList(String word) {
        char[] chars = word.toCharArray();
        List<String> list = new ArrayList<>();
        String tmp = "";

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            tmp += c;
            list.add(tmp);
        }

        return list;
    }
     */

    public boolean getCharsListContain(String word, String param) {
        char[] chars = word.toCharArray();
        String tmp = "";

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            tmp += c;

            if (param.equals(tmp)) {
                return true;
            }
        }

        return false;
    }
}
