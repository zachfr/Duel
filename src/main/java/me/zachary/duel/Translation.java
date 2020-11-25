package me.zachary.duel;

import xyz.theprogramsrc.supercoreapi.global.translations.TranslationManager;
import xyz.theprogramsrc.supercoreapi.global.translations.TranslationPack;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public enum Translation implements TranslationPack {
    /* MESSAGES */
    Ask_Yourself("&cYou can ask Yourself!"),
    Start_Duel("&9Duel start!"),
    Deny_Duel("&7You deny Duel"),
    Player_Deny_Duel("Player &e<PlayerDenyDuel> &rdeny duel!!"),
    Create_Arena("You create arena &a<ArenaName>"),
    No_Permission("&4You don't have permission"),
    Player_Already_Have_Requests("&cAttention this player already has a duel request in progress"),
    Ask_Player("&7You just asked a duel with &e<player>"),
    Receive_Duel("&9You have received a duel request from &r<player>"),
    Player_Not_Connected("Player &c<PlayerNotConnected>&f it's not connected or not exist"),
    No_Arena_Available("&cNo arena available!"),
    Duel_Start_Title("=&6Duel Start!"),
    Duel_Start_SubTitle("&eYour duel with &6<Player>"),
    Succesfull_Reload("&cYou have successfull reload the config!"),
    Broadcast_Duel_Win("&e<winner> &6won the duel against &e<loser>&6!"),
    Click_Button_Accept("&aClick to accept duel"),
    Click_Button_Deny("&cClick to deny duel"),
    Succesfull_Delete_Arena("&6You have been delete arena: &e<Arena>"),
    No_Arena_Found("&6No arena is found."),
    No_Argument_Delete_Arena("&6Please enter a arena to delete."),
    ;

    private final String content;
    private TranslationManager manager;

    Translation(String content){
        this.content = content;
    }

    @Override
    public Locale getLanguage() {
        return new Locale("en","US");
    }

    @Override
    public xyz.theprogramsrc.supercoreapi.global.translations.Translation get() {
        return new xyz.theprogramsrc.supercoreapi.global.translations.Translation(this, this.name(), this.content);
    }

    @Override
    public List<xyz.theprogramsrc.supercoreapi.global.translations.Translation> translations() {
        return Arrays.stream(values()).map(Translation::get).collect(Collectors.toList());
    }

    @Override
    public void setManager(TranslationManager manager) {
        this.manager = manager;
    }

    @Override
    public TranslationManager getManager() {
        return this.manager;
    }

    @Override
    public String toString() {
        return me.zachary.duel.Duel.i.getSettingsStorage().getPrefix() + this.get().translate();
    }
}
