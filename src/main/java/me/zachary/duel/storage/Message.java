package me.zachary.duel.storage;

import me.zachary.duel.Duel;
import xyz.theprogramsrc.supercoreapi.spigot.SpigotModule;
import xyz.theprogramsrc.supercoreapi.spigot.utils.storage.SpigotYMLConfig;

import java.util.ArrayList;
import java.util.List;

public class Message extends SpigotModule {
    private SpigotYMLConfig cfg;

    private Duel duel;
    public Message(Duel duel) {
        this.duel = duel;
    }

    @Override
    public void onLoad() {
        this.cfg = new SpigotYMLConfig(this.getPluginFolder(), "Messages.yml");
        this.loadDefaults();
    }

    private void loadDefaults(){
        this.cfg.add("Ask_Yourself", "&cYou can ask Yourself!");
        this.cfg.add("Start_Duel", "&9Duel start!");
        this.cfg.add("Deny_Duel", "&7You deny Duel");
        this.cfg.add("Player_Deny_Duel", "Player &e<PlayerDenyDuel> &rdeny duel!!");
        this.cfg.add("Create_Arena", "You create arena &a<ArenaName>");
        this.cfg.add("No_Permission", "&4You don't have permission");
        this.cfg.add("Player_Already_Have_Requests", "&cAttention this player already has a duel request in progress");
        this.cfg.add("Ask_Player", "&7You just asked a duel with &e<player>");
        this.cfg.add("Receive_Duel", "&9You have received a duel request from &r<player>");
        this.cfg.add("Player_Not_Connected", "Player &c<PlayerNotConnected>&f it's not connected or not exist");
        this.cfg.add("No_Arena_Available", "&cNo arena available!");
        this.cfg.add("Duel_Start_Title", "&6Duel Start!");
        this.cfg.add("Duel_Start_SubTitle", "&eYour duel with &6<Player>");
        this.cfg.add("HelpCommand", getHelpCommand());
    }

    public void getMessages() {
    }

    private List<String> getHelpCommand() {
        List<String> help = new ArrayList<>();
        help.add("&6--------------&e Duel &6----------------");
        help.add("&e/&rduel <player>");
        help.add("&e/&rduel <accept/deny>");
        help.add("&e/&rduel createarena <loc1> <loc2> <ArenaName>");
        help.add("&e/&rduel createarena <X,Y,Z> <X,Y,Z> <ArenaName>");
        help.add("&6------------------------------------");
        return help;
    }

    public String getString(String path) {
        return this.cfg.getString(path);
    }
}
