package me.zachary.duel.arenas;

import me.zachary.duel.Duel;
import me.zachary.duel.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ArenaManager {

    static Map<UUID, ItemStack[]> items = new HashMap<UUID, ItemStack[]>();
    static Map<UUID, ItemStack[]> armor = new HashMap<UUID, ItemStack[]>();
    static Map<String, Location> locations = new HashMap<String, Location>();

    private List<Arena> arenas = new ArrayList<>();
    private static Duel main;

    public ArenaManager(Duel duel) {
        this.main = duel;
    }

    public void addArena(Arena arena) {
        this.arenas.add(arena);
    }

    public void joinArena(Player firstPlayer, Player secondPlayer) {
        Arena nextArena = getNextArena();

        if (nextArena != null) {
            SaveLocations(firstPlayer);
            SaveLocations(secondPlayer);

            xyz.theprogramsrc.supercoreapi.spigot.packets.Title.sendTitle(firstPlayer, Utils.chat(main.getMessageConfig().getString("Duel_Start_Title")), Utils.chat(main.getMessageConfig().getString("Duel_Start_SubTitle").replace("<Player>", secondPlayer.getName())));
            xyz.theprogramsrc.supercoreapi.spigot.packets.Title.sendTitle(secondPlayer, Utils.chat(main.getMessageConfig().getString("Duel_Start_Title")), Utils.chat(main.getMessageConfig().getString("Duel_Start_SubTitle").replace("<Player>", firstPlayer.getName())));

            nextArena.getPlayers().add(firstPlayer);
            nextArena.getPlayers().add(secondPlayer);
            firstPlayer.teleport(nextArena.getFirstLoc());
            secondPlayer.teleport(nextArena.getSecondLoc());
            firstPlayer.setHealth(20);
            secondPlayer.setHealth(20);
            nextArena.setStarted();

            if (!main.getConfig().getBoolean("Player_Should_PVP_With_Their_Own_Stuff")) {
                storeAndClearInventory(firstPlayer);
                storeAndClearInventory(secondPlayer);

                addStuff(firstPlayer);
                addStuff(secondPlayer);
            }

        }else {
            firstPlayer.sendMessage(Utils.chat(main.getMessageConfig().getString("No_Arena_Available")));
            secondPlayer.sendMessage(Utils.chat(main.getMessageConfig().getString("No_Arena_Available")));
        }
    }

    public Arena getArenaByPlayer(Player player) {
        for (Arena arena : arenas) {
            if (arena.getPlayers().contains(player)) {
                return arena;
            }
        }
        return null;
    }

    private Arena getNextArena() {

        for (Arena arena : arenas) {
            if (!arena.isStarted()){
                return arena;
            }
        }

        return null;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public static void storeAndClearInventory(Player player){
        UUID uuid = player.getUniqueId();

        ItemStack[] cont = player.getInventory().getContents();
        ItemStack[] armcont = player.getInventory().getArmorContents();

        items.put(uuid, cont);
        armor.put(uuid, armcont);

        player.getInventory().clear();

        remArmor(player);
    }


    public void restoreInventory(Player player){
        UUID uuid = player.getUniqueId();

        ItemStack[] contents = items.get(uuid);
        ItemStack[] armorContents = armor.get(uuid);

        if(contents != null){
            player.getInventory().setContents(contents);
        }
        else{//if the player has no inventory contents, clear their inventory
            player.getInventory().clear();
        }

        if(armorContents != null){
            player.getInventory().setArmorContents(armorContents);
        }
        else{//if the player has no armor, set the armor to null
            remArmor(player);
        }
    }

    public static void remArmor(Player player){
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
    }

    public static void ClearMap(Player player) {
        items.clear();
        armor.clear();
    }

    public static void SaveLocations(Player player) {
        locations.put(player.getName(), player.getLocation());
    }

    public static void restoreLocations(Player player) {
        try {
            Location loc = locations.get(player.getName());
            player.teleport(loc);
        } catch (Exception e) {

        }
    }

    public static void addStuff(Player player) {
        player.getInventory().setHelmet(Utils.CreateItem(Material.valueOf(main.getConfig().getString("Stuff.Helmet"))));
        player.getInventory().setChestplate(Utils.CreateItem(Material.valueOf(main.getConfig().getString("Stuff.Chestplate"))));
        player.getInventory().setLeggings(Utils.CreateItem(Material.valueOf(main.getConfig().getString("Stuff.Leggings"))));
        player.getInventory().setBoots(Utils.CreateItem(Material.valueOf(main.getConfig().getString("Stuff.Boots"))));

        player.getInventory().addItem(Utils.CreateItem(Material.valueOf(main.getConfig().getString("Stuff.Sword"))));
    }


}
