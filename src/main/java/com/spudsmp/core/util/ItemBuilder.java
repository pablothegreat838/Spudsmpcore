package com.spudsmp.core.util;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemBuilder {
    private final ItemStack item;
    public ItemBuilder(Material material) { item = new ItemStack(material); }
    public ItemBuilder(Material material, int amount) { item = new ItemStack(material, amount); }
    public ItemBuilder name(String name) { ItemMeta meta = item.getItemMeta(); meta.setDisplayName(color(name)); item.setItemMeta(meta); return this; }
    public ItemBuilder lore(String... lore) { ItemMeta meta = item.getItemMeta(); meta.setLore(Arrays.asList(color(lore))); item.setItemMeta(meta); return this; }
    public ItemBuilder enchant(Enchantment enchantment) { item.addUnsafeEnchantment(enchantment, 1); return this; }
    public ItemBuilder hidden() { ItemMeta meta = item.getItemMeta(); meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS); item.setItemMeta(meta); return this; }
    public ItemStack build() { return item; }
    private static String color(String value) { return ChatColor.translateAlternateColorCodes('&', value); }
    private static String[] color(String[] values) { for (int i = 0; i < values.length; i++) values[i] = color(values[i]); return values; }
}