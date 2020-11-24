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

    public void getReloadMessage() {
        this.cfg.save();
        this.cfg.reload();
    }

    private void loadDefaults(){
        this.cfg.getConfig().options().header("GERMAN TRANSLATION: https://pastebin.com/7KZZDM5Y\n");
        this.cfg.getConfig().options().copyHeader(true);
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
        this.cfg.add("Succesfull_Reload", "&cYou have successfull reload the config!");
        this.cfg.add("Broadcast_Duel_Win", "&e<winner> &6won the duel against &e<loser>&6!");
        this.cfg.add("Click_Button_Accept", "&aClick to accept duel");
        this.cfg.add("Click_Button_Deny", "&cClick to deny duel");
        this.cfg.add("Succesfull_Delete_Arena", "&6You have been delete arena: &e<Arena>");
        this.cfg.add("No_Arena_Found", "&6No arena is found.");
        this.cfg.add("No_Argument_Delete_Arena", "&6Please enter a arena to delete.");
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

    public String getString(String path) {
        return this.cfg.getString(path);
    }

    public List<String> getStringList(String path){
        return this.cfg.getStringList(path);
    }
}
