package me.zachary.duel.commands;

import me.zachary.duel.arenas.Arena;
import me.zachary.duel.Duel;
import me.zachary.duel.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.spigot.commands.CommandResult;
import xyz.theprogramsrc.supercoreapi.spigot.commands.SpigotCommand;
import xyz.theprogramsrc.supercoreapi.spigot.utils.SpigotConsole;

import java.util.ArrayList;
import java.util.List;

public class Command extends SpigotCommand {
    private Duel duel;
    public Command(Duel duel) {
        this.duel = duel;
    }

    @Override
    public String getCommand() {
        return "duel";
    }

    @Override
    public CommandResult onPlayerExecute(Player player, String[] strings) {
        if (strings.length == 0 || strings[0].equalsIgnoreCase("help")) {
            for(String message : duel.getConfig().getStringList("HelpCommand")){
                player.sendMessage(Utils.chat(message));
            }
        }else if (strings[0].equalsIgnoreCase("accept")) {
            if (duel.players.containsKey(player)){
                player.sendMessage(Utils.chat(duel.getMessageConfig().getString("Start_Duel")));

                Player firstPlayerTarget = duel.players.get(player);
                firstPlayerTarget.sendMessage(Utils.chat(duel.getMessageConfig().getString("Start_Duel")));

                duel.arenaManager.joinArena(player, firstPlayerTarget);

                duel.players.remove(player);

            }
        }else if (strings[0].equalsIgnoreCase("deny")) {
            if (duel.players.containsKey(player)){
                player.sendMessage(Utils.chat(duel.getMessageConfig().getString("Deny_Duel")));

                Player firstPlayerTarget = duel.players.get(player);
                firstPlayerTarget.sendMessage(Utils.chat(duel.getMessageConfig().getString("Player_Deny_Duel").replace("<PlayerDenyDuel>", player.getName())));

                duel.players.remove(player);
            }
        }else if(strings[0].equalsIgnoreCase("createarena")) {
            if (player.hasPermission("duel.createarena")) {
                if (strings.length < 4) {
                    player.sendMessage("/duel createarena <loc1> <loc2> <ArenaName>");
                    return CommandResult.COMPLETED;
                }

                Location loc1 = parseStringToLoc(strings[1]);
                Location loc2 = parseStringToLoc(strings[2]);
                Arena arena = new Arena(loc1, loc2);
                String arenaName = "arena-" + strings[3];

                duel.arenaConfig.set("arenas." + arenaName + ".loc1", strings[1]);
                duel.arenaConfig.set("arenas." + arenaName + ".loc2", strings[2]);

                duel.saveArenaConfig();
                duel.arenaManager.addArena(arena);

                player.sendMessage(Utils.chat(duel.getMessageConfig().getString("Create_Arena").replace("<ArenaName>", arenaName)));
            }
        }else if (Bukkit.getPlayer(strings[0]) != null) {
            String targetName = strings[0];
            Player target = Bukkit.getPlayer(targetName);

            if (player == target) {
                player.sendMessage(Utils.chat(duel.getMessageConfig().getString("Ask_Yourself")));
                return CommandResult.COMPLETED;
            }

            if (duel.players.containsKey(target)){
                player.sendMessage(Utils.chat(duel.getMessageConfig().getString("Player_Already_Have_Requests")));
                return CommandResult.COMPLETED;
            }

            duel.players.put(target, player);
            player.sendMessage(Utils.chat(duel.getMessageConfig().getString("Ask_Player").replace("<player>", targetName)));
            target.sendMessage(Utils.chat(duel.getMessageConfig().getString("Receive_Duel").replace("<player>", player.getName())));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target.getName() + " [\"\",{\"text\":\"Click here to [ACCEPT]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/duel accept\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click to accept duel\"}]}}]");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target.getName() + " [\"\",{\"text\":\"Click here to [DENY]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/duel deny\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click to deny duel\"}]}}]");
        }else {
            player.sendMessage(Utils.chat(duel.getMessageConfig().getString("Player_Not_Connected").replace("<PlayerNotConnected>", strings[0])));
            return CommandResult.COMPLETED;
        }
        return CommandResult.COMPLETED;
    }

    @Override
    public CommandResult onConsoleExecute(SpigotConsole spigotConsole, String[] strings) {
        return CommandResult.COMPLETED;
    }

    public Location parseStringToLoc(String string) {
        String[] parsedLoc = string.split(",");
        double x = Double.valueOf(parsedLoc[0]);
        double y = Double.valueOf(parsedLoc[1]);
        double z = Double.valueOf(parsedLoc[2]);

        return new Location(Bukkit.getWorld("world"), x,y,z);
    }

    @Override
    public List<String> getCommandComplete(Player player, String alias, String[] args) {
        List<String> help = new ArrayList<>();
        Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().size()];
        Bukkit.getServer().getOnlinePlayers().toArray(players);
        for (int i = 0; i < players.length; i++) {
            help.add(players[i].getName());
        }
        help.add("help");
        if(player.hasPermission("duel.createarena")) help.add("createarena");
        return help;
    }
}
