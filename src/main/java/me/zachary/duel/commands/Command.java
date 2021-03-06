package me.zachary.duel.commands;

import me.zachary.duel.Duel;
import me.zachary.duel.Translation;
import me.zachary.duel.arenas.Arena;
import me.zachary.duel.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.spigot.commands.CommandResult;
import xyz.theprogramsrc.supercoreapi.spigot.commands.SpigotCommand;
import xyz.theprogramsrc.supercoreapi.spigot.utils.SpigotConsole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            if (duel.getConfig().getBoolean("Duel_Accept_Permission") && !player.hasPermission("duel.accept")){
                player.sendMessage(Utils.chat(Translation.No_Permission.toString()));
                return CommandResult.COMPLETED;
            }
            if (duel.players.containsKey(player)){
                player.sendMessage(Utils.chat(Translation.Start_Duel.toString()));

                Player firstPlayerTarget = duel.players.get(player);
                firstPlayerTarget.sendMessage(Utils.chat(Translation.Start_Duel.toString()));

                duel.arenaManager.joinArena(player, firstPlayerTarget);

                duel.players.remove(player);

            }
        }else if (strings[0].equalsIgnoreCase("deny")) {
            if (duel.getConfig().getBoolean("Duel_Deny_Permission") && !player.hasPermission("duel.deny")){
                player.sendMessage(Utils.chat(Translation.No_Permission.toString()));
                return CommandResult.COMPLETED;
            }
            if (duel.players.containsKey(player)){
                player.sendMessage(Utils.chat(Translation.Deny_Duel.toString()));

                Player firstPlayerTarget = duel.players.get(player);
                firstPlayerTarget.sendMessage(Utils.chat(Translation.Player_Deny_Duel.toString().replace("<PlayerDenyDuel>", player.getName())));

                duel.players.remove(player);
            }
        }else if(strings[0].equalsIgnoreCase("createarena")) {
            if (player.hasPermission("duel.createarena")) {
                if (strings.length < 4) {
                    player.sendMessage("/duel createarena <loc1> <loc2> <ArenaName>");
                    return CommandResult.COMPLETED;
                }

                String world = player.getLocation().getWorld().getName();
                Location loc1 = parseStringToLoc(strings[1], world);
                Location loc2 = parseStringToLoc(strings[2], world);
                Arena arena = new Arena(loc1, loc2);
                String arenaName = strings[3];

                duel.arenaConfig.set("arenas." + arenaName + ".loc1", strings[1]);
                duel.arenaConfig.set("arenas." + arenaName + ".loc2", strings[2]);
                duel.arenaConfig.set("arenas." + arenaName + ".world", world);

                duel.saveArenaConfig();
                duel.arenaManager.addArena(arena);

                player.sendMessage(Utils.chat(Translation.Create_Arena.toString().replace("<ArenaName>", arenaName)));
            }else
                player.sendMessage(Utils.chat(Translation.No_Permission.toString()));
        }else if(strings[0].equalsIgnoreCase("deletearena")){
            if(player.hasPermission("duel.deletearena")){
                if(strings.length <= 1){
                    player.sendMessage(Utils.chat(Translation.No_Argument_Delete_Arena.toString()));
                    return CommandResult.COMPLETED;
                }
                for(String string : duel.configurationSection().getKeys(false)) {
                    if(Objects.equals(string, strings[1])){
                        duel.configurationSection().set(string, null);
                        duel.saveArenaConfig();
                        player.sendMessage(Utils.chat(Translation.Succesfull_Delete_Arena.toString().replace("<Arena>", strings[1])));
                    }
                }
                duel.getArenaManager().clearArena();
                duel.getArenaManager().createArena();
            }else
                player.sendMessage(Utils.chat(Translation.No_Permission.toString()));
        }else if(strings[0].equalsIgnoreCase("listarena")){
            if(player.hasPermission("duel.listarena")){
                if(!duel.arenaConfig.contains("arenas") || duel.configurationSection().getKeys(false).isEmpty()){
                    player.sendMessage(Utils.chat(Translation.No_Arena_Found.toString()));
                    return CommandResult.COMPLETED;
                }
                for(String string : duel.configurationSection().getKeys(false)) {
                    player.sendMessage(string);
                }
            }else
                player.sendMessage(Utils.chat(Translation.No_Permission.toString()));
        }else if(strings[0].equalsIgnoreCase("cancel")){
            if(!player.hasPermission("duel.cancel")){
                player.sendMessage(Utils.chat(Translation.No_Permission.toString()));
                return CommandResult.COMPLETED;
            }
            final int[] i = {0};
            final Player[] target = new Player[1];
            duel.players.forEach((player1, player2) -> {
                if(player2 == player){
                    player1.sendMessage(Utils.chat(Translation.Cancel_Duel_Player.toString()));
                    player2.sendMessage(Utils.chat(Translation.Cancel_Duel_Asker.toString()));
                    target[0] = player1;
                    i[0] = 1;
                }
            });
            if(i[0] != 1){
                player.sendMessage(Utils.chat(Translation.No_Request_Pending.toString()));
                return CommandResult.COMPLETED;
            }else{
                duel.players.remove(target[0]);
            }
        }else if (Bukkit.getPlayer(strings[0]) != null) {
            if (duel.getConfig().getBoolean("Duel_<Player>_Permission") && !player.hasPermission("duel.askduel")){
                player.sendMessage(Utils.chat(Translation.No_Permission.toString()));
                return CommandResult.COMPLETED;
            }
            String targetName = strings[0];
            Player target = Bukkit.getPlayer(targetName);

            if (player == target) {
                player.sendMessage(Utils.chat(Translation.Ask_Yourself.toString()));
                return CommandResult.COMPLETED;
            }

            if (duel.players.containsKey(target)){
                player.sendMessage(Utils.chat(Translation.Player_Already_Have_Requests.toString()));
                return CommandResult.COMPLETED;
            }

            duel.players.put(target, player);
            TextComponent acceptbutton = new TextComponent(Utils.chat(Translation.Click_Button_Accept.options().get()));
            acceptbutton.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/duel accept" ) );
            TextComponent denybutton = new TextComponent(Utils.chat(Translation.Click_Button_Deny.options().get()));
            denybutton.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/duel deny" ) );

            player.sendMessage(Utils.chat(Translation.Ask_Player.toString().replace("<player>", targetName)));
            target.sendMessage(Utils.chat(Translation.Receive_Duel.toString().replace("<player>", player.getName())));
            target.spigot().sendMessage(acceptbutton);
            target.spigot().sendMessage(denybutton);
        } else if (strings[0].equalsIgnoreCase("reload")) {
            if (player.hasPermission("duel.reload")) {
                duel.saveDefaultConfig();
                duel.reloadConfig();
                player.sendMessage(Utils.chat(Translation.Succesfull_Reload.toString()));
            }else
                player.sendMessage(Utils.chat(Translation.No_Permission.toString()));
        } else {
            player.sendMessage(Utils.chat(Translation.Player_Not_Connected.toString().replace("<PlayerNotConnected>", strings[0])));
            return CommandResult.COMPLETED;
        }
        return CommandResult.COMPLETED;
    }

    @Override
    public CommandResult onConsoleExecute(SpigotConsole spigotConsole, String[] strings) {
        return CommandResult.COMPLETED;
    }

    public Location parseStringToLoc(String string, String world) {
        String[] parsedLoc = string.split(",");
        double x = Double.valueOf(parsedLoc[0]);
        double y = Double.valueOf(parsedLoc[1]);
        double z = Double.valueOf(parsedLoc[2]);

        return new Location(Bukkit.getWorld(world), x,y,z);
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
        if(player.hasPermission("duel.deletearena")) help.add("deletearena");
        if(player.hasPermission("duel.listarena")) help.add("listarena");
        if(player.hasPermission("duel.cancel")) help.add("cancel");
        if(player.hasPermission("duel.reload")) help.add("reload");
        return help;
    }
}
