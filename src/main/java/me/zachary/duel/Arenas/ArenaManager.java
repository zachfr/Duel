package me.zachary.duel.Arenas;

import me.zachary.duel.Utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ArenaManager {

    static Map<UUID, ItemStack[]> items = new HashMap<UUID, ItemStack[]>();
    static Map<UUID, ItemStack[]> armor = new HashMap<UUID, ItemStack[]>();

    private List<Arena> arenas = new ArrayList<>();

    public void addArena(Arena arena) {
        this.arenas.add(arena);
    }

    public void joinArena(Player firstPlayer, Player secondPlayer) {
        Arena nextArena = getNextArena();

        if (nextArena != null) {

            nextArena.getPlayers().add(firstPlayer);
            nextArena.getPlayers().add(secondPlayer);
            firstPlayer.teleport(nextArena.getFirstLoc());
            secondPlayer.teleport(nextArena.getSecondLoc());
            nextArena.setStarted();

            storeAndClearInventory(firstPlayer);
            storeAndClearInventory(secondPlayer);

            addStuff(firstPlayer);
            addStuff(secondPlayer);

        }else {
            firstPlayer.sendMessage("§cNo arena available!");
            secondPlayer.sendMessage("§cNo arena available!");
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

    public static void addStuff(Player player) {
        player.getInventory().setHelmet(Utils.CreateItem(Material.DIAMOND_HELMET));
        //player.getInventory().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
        //player.getInventory().setChestplate(Utils.CreateItem(Material.matchMaterial(duel.getConfig().getString("Chestplate"))));
        //player.getInventory().setLeggings(Utils.CreateItem(Material.matchMaterial(duel.getConfig().getString("Leggings"))));
        //player.getInventory().setBoots(Utils.CreateItem(Material.matchMaterial(duel.getConfig().getString("Boots"))));

        //player.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
        player.getInventory().addItem(Utils.CreateItem(Material.matchMaterial("DIAMOND_SWORD")));
    }


}
