package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import com.spudsmp.core.util.ItemBuilder;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ShopManager implements Listener {
    private final SpudSMPPlugin plugin;
    public ShopManager(SpudSMPPlugin plugin) { this.plugin = plugin; }
    public ConfigurationSection categories() { return plugin.getConfig().getConfigurationSection("shop.categories"); }
    public double price(String category, Material material, boolean buy) { List<?> values = plugin.getConfig().getList("shop.categories." + category + "." + material.name()); if (values == null || values.size() < 2) return 0; return ((Number) values.get(buy ? 0 : 1)).doubleValue(); }
    public void open(Player player) { Inventory inventory = Bukkit.createInventory(null, 54, "SpudSMP Shop"); int slot = 0; for (String category : categories().getKeys(false)) { inventory.setItem(slot++, new ItemBuilder(Material.CHEST).name("&6" + category.substring(0, 1).toUpperCase() + category.substring(1)).build()); } inventory.setItem(53, new ItemBuilder(Material.SKULL_ITEM).name("&aBalance").lore("&7$" + String.format("%.2f", plugin.economy().balance(player))).build()); player.openInventory(inventory); }
    private void openCategory(Player player, String category) { Inventory inventory = Bukkit.createInventory(null, 54, "SpudSMP Shop: " + category); ConfigurationSection section = plugin.getConfig().getConfigurationSection("shop.categories." + category); int slot = 0; for (String raw : section.getKeys(false)) { try { Material material = Material.valueOf(raw); inventory.setItem(slot++, new ItemBuilder(material).name("&e" + raw).lore("&7Buy: $" + price(category, material, true), "&7Click to buy 1").build()); } catch (IllegalArgumentException ignored) {} } inventory.setItem(53, new ItemBuilder(Material.SKULL_ITEM).name("&aBalance").lore("&7$" + String.format("%.2f", plugin.economy().balance(player))).build()); player.openInventory(inventory); }
    @EventHandler public void click(InventoryClickEvent event) { if (!(event.getWhoClicked() instanceof Player)) return; String title = event.getView().getTitle(); if (!title.startsWith("SpudSMP Shop")) return; event.setCancelled(true); Player player = (Player) event.getWhoClicked(); if (event.getRawSlot() >= 53 || event.getCurrentItem() == null) return; if (title.equals("SpudSMP Shop")) { List<String> categories = new ArrayList<String>(this.categories().getKeys(false)); if (event.getRawSlot() < categories.size()) openCategory(player, categories.get(event.getRawSlot())); return; } String category = title.substring("SpudSMP Shop: ".length()); ItemStack item = event.getCurrentItem(); Material material = item.getType(); double cost = price(category, material, true); if (cost > 0 && plugin.economy().take(player, cost)) { player.getInventory().addItem(new ItemStack(material)); player.sendMessage(plugin.messages().prefix() + ChatColor.GREEN + "Purchased " + material.name() + " for $" + cost + "."); } else player.sendMessage(plugin.messages().prefix() + ChatColor.RED + "You cannot afford that item."); }
}
