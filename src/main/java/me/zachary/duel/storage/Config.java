package me.zachary.duel.storage;

import me.zachary.duel.Duel;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotModule;
import xyz.theprogramsrc.supercoreapi.spigot.utils.storage.SpigotYMLConfig;

import java.util.ArrayList;
import java.util.List;

public class Config extends SpigotModule {
    private Duel duel;
    private SpigotYMLConfig cfg;

    public Config(Duel duel) {
        this.duel = duel;
    }

    @Override
    public void onLoad() {
        this.cfg = new SpigotYMLConfig(this.getPluginFolder(), "config.yml");
        this.loadDefaults();
    }

    public void getReloadConfig() {
        this.cfg.save();
        this.cfg.reload();
    }


    private void loadDefaults() {
        this.cfg.getConfig().options().header("You can edit messsage in messages.yml file.\nYou can configure Stuff during duel below.");
        this.cfg.getConfig().options().copyHeader(true);
        this.cfg.add("Player_Should_PVP_With_Their_Own_Stuff", false);
        this.cfg.add("Should_Killed_Player_Should_Drop_There_Stuff", false);
        this.cfg.add("Should_PvP_Is_Only_Enable_During_Duel", true);
        this.cfg.add("Stuff.Helmet.name", "DIAMOND_HELMET");
        this.cfg.add("Stuff.Helmet.enchantment.name", "PROTECTION");
        this.cfg.add("Stuff.Helmet.enchantment.level", 1);
        this.cfg.add("Stuff.Chestplate.name", "DIAMOND_CHESTPLATE");
        this.cfg.add("Stuff.Chestplate.enchantment.name", "null");
        this.cfg.add("Stuff.Chestplate.enchantment.level", 1);
        this.cfg.add("Stuff.Leggings.name", "DIAMOND_LEGGINGS");
        this.cfg.add("Stuff.Boots.name", "DIAMOND_BOOTS");
        this.cfg.add("Stuff.Content.0.name", "DIAMOND_SWORD");
        this.cfg.add("Stuff.Content.0.enchantment.name", "SHARPNESS");
        this.cfg.add("Stuff.Content.0.enchantment.level", 1);
        this.cfg.add("Stuff.Content.1.name", "GOLDEN_APPLE");
        this.cfg.add("Stuff.Content.1.amount", 3);
        this.cfg.add("Particle_When_Player_Win_Duel.Enable", true);
        this.cfg.add("Particle_When_Player_Win_Duel.Particle", "EXPLOSION_HUGE");
        this.cfg.add("Duel_<Player>_Permission", false);
        this.cfg.add("Duel_Accept_Permission", false);
        this.cfg.add("Duel_Deny_Permission", false);
        this.cfg.add("HelpCommand", getHelpCommand());
    }

    private List<String> getHelpCommand() {
        List<String> help = new ArrayList<>();
        help.add("&6--------------&e Duel &6----------------");
        help.add("&e/&rduel <player>");
        help.add("&e/&rduel <accept/deny>");
        help.add("&e/&rduel createarena <loc1> <loc2> <ArenaName>");
        help.add("&e/&rduel createarena <X,Y,Z> <X,Y,Z> <ArenaName>");
        help.add("&e/&rduel deletearena <ArenaName>");
        help.add("&e/&rduel listarena");
        help.add("&6------------------------------------");
        return help;
    }

    public List<String> getStringList(String path){
        return this.cfg.getStringList(path);
    }
}
