package me.zachary.duel;

import me.zachary.duel.Arenas.Arena;
import me.zachary.duel.Arenas.ArenaListeners;
import me.zachary.duel.Arenas.ArenaManager;
import me.zachary.duel.Utils.Metrics;
import me.zachary.duel.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import sun.applet.Main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Duel extends JavaPlugin {

    private Map<Player, Player> players = new HashMap<>();
    private ArenaManager arenaManager = new ArenaManager(this);
    private File arenaFile;
    private YamlConfiguration arenaConfig;
    private Main plugin;
    //test

    public Duel() {
    }

    @Override
    public void onEnable() {
        int pluginId = 9146;
        Metrics metrics = new Metrics(this, pluginId);

        getCommand("duel").setExecutor(this);
        getServer().getPluginManager().registerEvents(new ArenaListeners(this), this);

        loadArenaConfig();
        saveDefaultConfig();

        ConfigurationSection arenaSection = arenaConfig.getConfigurationSection("arenas");

        try {
            for(String string : arenaSection.getKeys(false)) {
                String loc1 = (String) arenaSection.get(string + ".loc1");
                String loc2 = (String) arenaSection.get(string + ".loc2");
                Arena arena = new Arena(parseStringToLoc(loc1), parseStringToLoc(loc2));
                arenaManager.addArena(arena);
            }
        }
        catch(Exception e) {
            System.out.print("[Duel] You don't have create arena yet!");
        }

    }

    private void loadArenaConfig() {
        if (!getDataFolder().exists()){
            getDataFolder().mkdir();
        }

        arenaFile = new File(getDataFolder() + File.separator + "arenas.yml");

        if (!arenaFile.exists()) {
            try {
                arenaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        arenaConfig = YamlConfiguration.loadConfiguration(arenaFile);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (label.equalsIgnoreCase("duel") && sender instanceof Player){
            Player player = (Player) sender;

            if (args.length == 0) {
                for(String message : this.getConfig().getStringList("HelpCommand")){
                    sender.sendMessage(Utils.chat(message));
                }
                return true;
            }
            if (args.length >= 1){
                String targetName = args[0];

                if (args[0].equalsIgnoreCase("accept")){

                    if (players.containsKey(player)){
                        player.sendMessage(Utils.chat(this.getConfig().getString("Start_Duel")));

                        Player firstPlayerTarget = players.get(player);
                        firstPlayerTarget.sendMessage(Utils.chat(this.getConfig().getString("Start_Duel")));

                        arenaManager.joinArena(player, firstPlayerTarget);

                        players.remove(player);

                    }


                }else if (args[0].equalsIgnoreCase("deny")){

                    if (players.containsKey(player)){
                        player.sendMessage(Utils.chat(this.getConfig().getString("Deny_Duel")));

                        Player firstPlayerTarget = players.get(player);
                        firstPlayerTarget.sendMessage(Utils.chat(this.getConfig().getString("Player_Deny_Duel").replace("<PlayerDenyDuel>", player.getName())));

                        players.remove(player);
                    }

                } else if (args[0].equalsIgnoreCase("createarena")) {
                    if (player.hasPermission("duel.createarena")) {
                        if (args.length < 4) {
                            player.sendMessage("/duel createarena <loc1> <loc2> <ArenaName>");
                            return true;
                        }

                        Location loc1 = parseStringToLoc(args[1]);
                        Location loc2 = parseStringToLoc(args[2]);
                        Arena arena = new Arena(loc1, loc2);
                        String arenaName = "arena-" + args[3];

                        arenaConfig.set("arenas." + arenaName + ".loc1", args[1]);
                        arenaConfig.set("arenas." + arenaName + ".loc2", args[2]);

                        saveArenaConfig();
                        arenaManager.addArena(arena);

                        player.sendMessage(Utils.chat(this.getConfig().getString("Create_Arena").replace("<ArenaName>", arenaName)));
                    }else {
                        player.sendMessage(Utils.chat(this.getConfig().getString("No_Permission")));
                    }


                } /*else if (args[0].equalsIgnoreCase("arenalist")) {
                    if (player.hasPermission("duel.arenalist")) {
                        player.sendMessage("yea hi");
                    }else {
                        player.sendMessage(this.getConfig().getString("No_Permission"));
                    }
                }*/ else if (Bukkit.getPlayer(targetName) != null){
                    Player target = Bukkit.getPlayer(targetName);

                    if (player == target) {
                        player.sendMessage(Utils.chat(this.getConfig().getString("ask_yourself")));
                        return true;
                    }

                    if (players.containsKey(target)){
                        player.sendMessage(Utils.chat(this.getConfig().getString("Player_Already_Have_Requests")));
                        return true;
                    }

                    players.put(target, player);
                    player.sendMessage(Utils.chat(this.getConfig().getString("Ask_Player").replace("<player>", targetName)));
                    target.sendMessage(Utils.chat(this.getConfig().getString("Receive_Duel").replace("<player>", player.getName())));
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target.getName() + " [\"\",{\"text\":\"Click here to [ACCEPT]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/duel accept\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click to accept duel\"}]}}]");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + target.getName() + " [\"\",{\"text\":\"Click here to [DENY]\",\"color\":\"red\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/duel deny\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\"Click to deny duel\"}]}}]");

                }else {
                    player.sendMessage(Utils.chat(this.getConfig().getString("Player_Not_Connected").replace("<PlayerNotConnected>", targetName)));
                }

                return true;
            }
        }
        return false;
        //return super.onCommand(sender, command, label, args);
    }

    private void saveArenaConfig() {
        try {
            arenaConfig.save(arenaFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public Location parseStringToLoc(String string) {
        String[] parsedLoc = string.split(",");
        double x = Double.valueOf(parsedLoc[0]);
        double y = Double.valueOf(parsedLoc[1]);
        double z = Double.valueOf(parsedLoc[2]);

        return new Location(Bukkit.getWorld("world"), x,y,z);
    }

    public String unparseLocToString(Location loc) {
        return loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
