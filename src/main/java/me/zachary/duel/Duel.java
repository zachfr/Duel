package me.zachary.duel;

import me.zachary.duel.Arenas.Arena;
import me.zachary.duel.Arenas.ArenaListeners;
import me.zachary.duel.Arenas.ArenaManager;
import me.zachary.duel.Commands.Command;
import me.zachary.duel.Utils.Metrics;
import me.zachary.duel.Utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import sun.applet.Main;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Duel extends SpigotPlugin {

    public Map<Player, Player> players = new HashMap<>();
    public ArenaManager arenaManager = new ArenaManager(this);
    private File arenaFile;
    public YamlConfiguration arenaConfig;
    private Main plugin;

    public Duel() {
    }

    @Override
    public void onPluginEnable() {
        int pluginId = 9146;
        Metrics metrics = new Metrics(this, pluginId);

        getServer().getPluginManager().registerEvents(new ArenaListeners(this), this);
        new Command(this);

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

    public void loadArenaConfig() {
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

    public void saveArenaConfig() {
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
    public void onPluginDisable() {
        // Plugin shutdown logic
    }

    @Override
    public void onPluginLoad() {

    }


}
