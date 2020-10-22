package me.zachary.duel.storage;

import me.zachary.duel.Duel;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotModule;
import xyz.theprogramsrc.supercoreapi.spigot.utils.storage.SpigotYMLConfig;

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

    private void loadDefaults() {
        this.cfg.getConfig().options().header("You can edit messsage in messages.yml file.\nYou can configure Stuff during duel below.");
        this.cfg.getConfig().options().copyHeader(true);
        this.cfg.add("Player_Should_PVP_With_Their_Own_Stuff", false);
        this.cfg.add("Stuff.Helmet", "DIAMOND_HELMET");
        this.cfg.add("Stuff.Chestplate", "DIAMOND_CHESTPLATE");
        this.cfg.add("Stuff.Leggings", "DIAMOND_LEGGINGS");
        this.cfg.add("Stuff.Boots", "DIAMOND_BOOTS");
        this.cfg.add("Stuff.Sword", "DIAMOND_SWORD");
    }
}
