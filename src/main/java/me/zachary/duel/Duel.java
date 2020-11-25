package me.zachary.duel;

import me.zachary.duel.arenas.Arena;
import me.zachary.duel.arenas.ArenaListeners;
import me.zachary.duel.arenas.ArenaManager;
import me.zachary.duel.commands.Command;
import me.zachary.duel.storage.Config;
import me.zachary.duel.utils.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import xyz.theprogramsrc.supercoreapi.global.translations.TranslationDownloader;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Duel extends SpigotPlugin {

    public Map<Player, Player> players = new HashMap<>();
    public ArenaManager arenaManager = new ArenaManager(this);
    public Arena arena = new Arena(this);
    private File arenaFile;
    public YamlConfiguration arenaConfig;
    private Duel plugin;
    private String world;
    private static Duel duel;

    public Duel() {
    }

    @Override
    public void onPluginEnable() {
        int pluginId = 9146;
        Metrics metrics = new Metrics(this, pluginId);
        duel = this;

        getServer().getPluginManager().registerEvents(new ArenaListeners(this), this);
        new Command(this);
        new Config(this);

        this.getTranslationManager().registerTranslation(Translation.class);
        TranslationDownloader.downloadFromGitHub(this, "zachfr", "Duel-Translations", "Translations");
        log("Successfully loaded translations: " + getLanguage());

        loadArenaConfig();
        saveDefaultConfig();

        ConfigurationSection arenaSection = arenaConfig.getConfigurationSection("arenas");

        try {
            for(String string : arenaSection.getKeys(false)) {
                String loc1 = (String) arenaSection.get(string + ".loc1");
                String loc2 = (String) arenaSection.get(string + ".loc2");
                if(arenaSection.get(string + ".world") != null) {
                    world = (String) arenaSection.get(string + ".world");
                }else {
                    world = "world";
                }
                Arena arena = new Arena(parseStringToLoc(loc1, world), parseStringToLoc(loc2, world));
                arenaManager.addArena(arena);
            }
        }
        catch(Exception e) {
            log("You don't have create arena yet!");
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

    public Config getConfigFile() { return new Config(this); }

    public Location parseStringToLoc(String string, String world) {
        String[] parsedLoc = string.split(",");
        double x = Double.valueOf(parsedLoc[0]);
        double y = Double.valueOf(parsedLoc[1]);
        double z = Double.valueOf(parsedLoc[2]);

        return new Location(Bukkit.getWorld(world), x,y,z);
    }

    public String unparseLocToString(Location loc) {
        return loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    @Override
    public void onPluginDisable() {
        // Plugin shutdown logic.
    }

    @Override
    public void onPluginLoad() {
        // On plugin load.
    }

    @Override
    public boolean isPaid() {
        return false;
    }

    public Duel getMain(){
        return duel;
    }

    public ConfigurationSection configurationSection(){
        return arenaConfig.getConfigurationSection("arenas");
    }
}
