package me.zachary.duel.arenas;

import com.shampaggon.crackshot.CSUtility;
import me.zachary.duel.Duel;
import me.zachary.duel.Translation;
import me.zachary.duel.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

    public void clearArena(){
        this.arenas.clear();
    }

    public void createArena(){
        String world;
        try {
            for(String string : main.configurationSection().getKeys(false)) {
                String loc1 = (String) main.configurationSection().get(string + ".loc1");
                String loc2 = (String) main.configurationSection().get(string + ".loc2");
                if(main.configurationSection().get(string + ".world") != null) {
                    world = (String) main.configurationSection().get(string + ".world");
                }else {
                    world = "world";
                }
                Arena arena = new Arena(parseStringToLoc(loc1, world), parseStringToLoc(loc2, world));
                this.addArena(arena);
            }
        }
        catch(Exception e) {
            System.out.println("You don't have create arena yet!");
        }
    }

    public void addArena(Arena arena) {
        this.arenas.add(arena);
    }

    public void joinArena(Player firstPlayer, Player secondPlayer) {
        Arena nextArena = getNextArena();

        if (nextArena != null) {
            SaveLocations(firstPlayer);
            SaveLocations(secondPlayer);

            xyz.theprogramsrc.supercoreapi.spigot.packets.Title.sendTitle(firstPlayer, Utils.chat(Translation.Duel_Start_Title.options().get()), Utils.chat(Translation.Duel_Start_SubTitle.options().get().replace("<Player>", secondPlayer.getName())));
            xyz.theprogramsrc.supercoreapi.spigot.packets.Title.sendTitle(secondPlayer, Utils.chat(Translation.Duel_Start_Title.options().get()), Utils.chat(Translation.Duel_Start_SubTitle.options().get().replace("<Player>", firstPlayer.getName())));

            nextArena.getPlayers().add(firstPlayer);
            nextArena.getPlayers().add(secondPlayer);
            firstPlayer.teleport(nextArena.getFirstLoc());
            secondPlayer.teleport(nextArena.getSecondLoc());
            firstPlayer.setGameMode(GameMode.SURVIVAL);
            secondPlayer.setGameMode(GameMode.SURVIVAL);
            firstPlayer.setHealth(20);
            secondPlayer.setHealth(20);
            firstPlayer.setFoodLevel(20);
            secondPlayer.setFoodLevel(20);
            nextArena.setStarted();

            if (!main.getConfig().getBoolean("Player_Should_PVP_With_Their_Own_Stuff")) {
                storeAndClearInventory(firstPlayer);
                storeAndClearInventory(secondPlayer);

                addStuff(firstPlayer);
                addStuff(secondPlayer);
            }

        }else {
            firstPlayer.sendMessage(Utils.chat(Translation.No_Arena_Available.toString()));
            secondPlayer.sendMessage(Utils.chat(Translation.No_Arena_Available.toString()));
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
        else{ //if the player has no inventory contents, clear their inventory
            player.getInventory().clear();
        }

        if(armorContents != null){
            player.getInventory().setArmorContents(armorContents);
        }
        else{ //if the player has no armor, set the armor to null
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
        Location loc = locations.get(player.getName());
        player.teleport(loc);

    }

    public static void addStuff(Player player) {
        ItemStack Helmet = new ItemStack(Material.valueOf(main.getConfig().getString("Stuff.Helmet.name")));
        ItemStack Chestplate = new ItemStack(Material.valueOf(main.getConfig().getString("Stuff.Chestplate.name")));
        ItemStack Leggings = new ItemStack(Material.valueOf(main.getConfig().getString("Stuff.Leggings.name")));
        ItemStack Boots = new ItemStack(Material.valueOf(main.getConfig().getString("Stuff.Boots.name")));
        if(main.getConfig().getString("Stuff.Helmet.enchantment") != null) {
            com.cryptomorin.xseries.XEnchantment.addEnchantFromString(Helmet, main.getConfig().getString("Stuff.Helmet.enchantment.name") + ", " + main.getConfig().getString("Stuff.Helmet.enchantment.level"));
        }
        if(main.getConfig().getString("Stuff.Chestplate.enchantment") != null) {
            com.cryptomorin.xseries.XEnchantment.addEnchantFromString(Chestplate, main.getConfig().getString("Stuff.Chestplate.enchantment.name") + ", " + main.getConfig().getString("Stuff.Chestplate.enchantment.level"));
        }
        if(main.getConfig().getString("Stuff.Leggings.enchantment") != null) {
            com.cryptomorin.xseries.XEnchantment.addEnchantFromString(Leggings, main.getConfig().getString("Stuff.Leggings.enchantment.name") + ", " + main.getConfig().getString("Stuff.Leggings.enchantment.level"));
        }
        if(main.getConfig().getString("Stuff.Boots.enchantment") != null) {
            com.cryptomorin.xseries.XEnchantment.addEnchantFromString(Boots, main.getConfig().getString("Stuff.Boots.enchantment.name") + ", " + main.getConfig().getString("Stuff.Boots.enchantment.level"));
        }

        player.getInventory().setHelmet(Helmet);
        player.getInventory().setChestplate(Chestplate);
        player.getInventory().setLeggings(Leggings);
        player.getInventory().setBoots(Boots);

        for (int iItem = 0; iItem <= main.getConfig().getString("Stuff.Content").length(); iItem++) {
            if(main.getConfig().getString("Stuff.Content." + iItem) != null) {
                if(main.getConfig().contains("Stuff.Content." + iItem + ".crackshot") && main.getConfig().getBoolean("Stuff.Content." + iItem + ".crackshot")){
                    main.getcsUtility().giveWeapon(player, main.getConfig().getString("Stuff.Content." + iItem + ".name"), 1);
                }else {
                    ItemStack item = new ItemStack(Material.valueOf(main.getConfig().getString("Stuff.Content." + iItem + ".name")));
                    if(main.getConfig().getString("Stuff.Content." + iItem + ".enchantment") != null) com.cryptomorin.xseries.XEnchantment.addEnchantFromString(item, main.getConfig().getString("Stuff.Content." + iItem + ".enchantment.name") + ", " + main.getConfig().getString("Stuff.Content." + iItem + ".enchantment.level"));
                    if(main.getConfig().getString("Stuff.Content." + iItem + ".amount") != null) item.setAmount(Integer.parseInt(main.getConfig().getString("Stuff.Content." + iItem + ".amount")));

                    player.getInventory().addItem(item);
                }
            }
        }
    }

    public Location parseStringToLoc(String string, String world) {
        String[] parsedLoc = string.split(",");
        double x = Double.valueOf(parsedLoc[0]);
        double y = Double.valueOf(parsedLoc[1]);
        double z = Double.valueOf(parsedLoc[2]);

        return new Location(Bukkit.getWorld(world), x,y,z);
    }

}
