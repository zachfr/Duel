package me.zachary.duel.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Utils {
    public static String chat (String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static ItemStack CreateItem(Material material) {
        ItemStack item = new ItemStack(material);
        return item;
    }
}
