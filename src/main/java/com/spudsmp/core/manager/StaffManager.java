package com.spudsmp.core.manager;

import com.spudsmp.core.SpudSMPPlugin;
import com.spudsmp.core.util.ItemBuilder;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

public final class StaffManager implements Listener {
    private final SpudSMPPlugin plugin;
    private final Set<UUID> mode = new HashSet<UUID>();
    private final Set<UUID> vanished = new HashSet<UUID>();
    public StaffManager(SpudSMPPlugin plugin) { this.plugin = plugin; }
    public void open(Player player) { Inventory inventory = Bukkit.createInventory(null, 9, ChatColor.DARK_GRAY + "SpudSMP Staff"); inventory.setItem(2, new ItemBuilder(Material.BOOK).name("&eReports").build()); inventory.setItem(4, new ItemBuilder(Material.PAPER).name("&fNotes").build()); inventory.setItem(6, new ItemBuilder(Material.DIAMOND).name("&bStaff Mode").build()); player.openInventory(inventory); }
    public void toggle(Player player) { if (mode.remove(player.getUniqueId())) { player.getInventory().clear(); player.sendMessage(plugin.messages().prefix() + ChatColor.RED + "Staff mode disabled."); } else { mode.add(player.getUniqueId()); player.getInventory().setItem(0, new ItemBuilder(Material.DIAMOND).name("&bPTP").build()); player.getInventory().setItem(4, new ItemBuilder(Material.POTION).name("&dVanish").build()); player.getInventory().setItem(7, new ItemBuilder(Material.PACKED_ICE).name("&bFreeze").build()); player.getInventory().setItem(8, new ItemBuilder(Material.DIAMOND_SWORD).name("&cBan Hammer").build()); player.sendMessage(plugin.messages().prefix() + ChatColor.GREEN + "Staff mode enabled."); } }
    public boolean isMode(Player player) { return mode.contains(player.getUniqueId()); }
    @EventHandler public void click(InventoryClickEvent event) { if (!event.getView().getTitle().contains("Staff")) return; event.setCancelled(true); if (event.getWhoClicked() instanceof Player && event.getRawSlot() == 6) toggle((Player) event.getWhoClicked()); }
    @EventHandler public void quit(PlayerQuitEvent event) { mode.remove(event.getPlayer().getUniqueId()); vanished.remove(event.getPlayer().getUniqueId()); }
}